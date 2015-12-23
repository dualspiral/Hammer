/*
 * This file is part of Hammer, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Daniel Naylor
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.co.drnaylor.minecraft.hammer.core.commands;

import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerPermissions;
import uk.co.drnaylor.minecraft.hammer.core.audit.ActionEnum;
import uk.co.drnaylor.minecraft.hammer.core.audit.AuditEntry;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.BanFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreateBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreateBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

import java.text.MessageFormat;
import java.util.*;

@RunAsync
public abstract class BaseBanCommandCore extends CommandCore {

    BaseBanCommandCore(HammerCore core) {
        super(core);
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
    }

    /**
     * Executes the specific command. Code here is common to all player bans
     * 
     * @param source The source that is executing this command.
     * @param arguments The arguments. Expecting 2+. [-a] player reason
     * @return <code>true</code> if the command does the expected task. <code>false</code> if the specific
     *         implementation command handler needs to take over.
     * 
     * @throws HammerException Thrown in place of all other exceptions.
     */
    @Override
    public final boolean executeCommand(WrappedCommandSource source, ArgumentMap arguments, DatabaseConnection conn) throws HammerException {
        WrappedServer server = core.getWrappedServer();

        ConfigurationNode cn = core.getConfig().getConfig();
        int serverId = cn.getNode("server", "id").getInt();
        HammerCreateBanBuilder builder = new HammerCreateBanBuilder(source.getUUID(), serverId, cn.getNode("server", "name").getString("Unknown"));

        // Next up, the player. Can we find them? Get the last player...
        UUID uuidToBan = arguments.<List<HammerPlayerInfo>>getArgument("player").get().stream().sorted(Collections.reverseOrder()).findFirst().get().getUUID();
        Optional<List<BanFlagEnum>> flags = arguments.<List<BanFlagEnum>>getArgument("flags");
        List<BanFlagEnum> flag;
        if (flags.isPresent()) {
            flag = flags.get();
        } else {
            flag = Collections.emptyList();
        }

        // Permission check
        if (flag.contains(BanFlagEnum.PERM) && !source.hasPermission("hammer.ban.perm")) {
            sendNoPermsMessage(source);
            return true;
        }

        if (flag.contains(BanFlagEnum.ALL) && !source.hasPermission("hammer.ban.all")) {
            sendNoPermsMessage(source);
            return true;
        }

        // Start a transaction. We might need to delete some rows here.
        conn.startTransaction();

        builder.setPlayerToBan(uuidToBan);

        // Set global flag here, before the checks below.
        builder.setAll(flag.contains(BanFlagEnum.ALL));

        // Same with permanent ban flag
        builder.setPerm(flag.contains(BanFlagEnum.PERM));
        builder.setReason(arguments.<String>getArgument("reason").orElse(null));

        if (!performSpecificActions(builder, arguments)) {
            // Usage.
            sendUsageMessage(source);
            conn.rollbackTransaction();
            return true;
        }

        // Check current ban status.
        BanInfo status = checkOtherBans(uuidToBan, conn, builder);

        if (status.status == BanStatus.NO_ACTION) {
            sendTemplatedMessage(source, "hammer.player.alreadyBanned", true, true);
            return true;
        } else if (status.status == BanStatus.TO_PERM) {
            // Auto upgrade! If you get a global ban, and a permanent ban is already in force, the 
            // permanent ban takes effect everywhere.
            sendTemplatedMessage(source, "hammer.player.upgradeToPerm", false, false);
            conn.getBanHandler().unbanFromServer(uuidToBan, serverId);
            builder.setPerm(true);
        } else if (status.status == BanStatus.TO_GLOBAL) {
            // Auto upgrade! If you get a perm ban, and a global ban is already in force, the 
            // permanent ban takes effect everywhere.
            sendTemplatedMessage(source, "hammer.player.upgradeToAll", false, false);
            conn.getBanHandler().unbanFromAllServers(uuidToBan);
            builder.setAll(true);
        }

        String reason = createReason(arguments, status.reasons);
        if (reason == null) {
            // Usage.
            sendUsageMessage(source);
            conn.rollbackTransaction();
            return true;
        }

        builder.setReason(reason);
        builder.setExternalId(conn.getNewExternalID());

        HammerCreateBan builtban = builder.build();
        if (!(builtban instanceof HammerCreateBan.Player)) {
            throw new HammerException("Could not build a player ban");
        }

        HammerCreateBan.Player ban = (HammerCreateBan.Player)builtban;
        conn.getBanHandler().createPlayerBan(ban);

        // Commit the transaction
        conn.commitTransaction();

        // Now, ban the player!
        WrappedPlayer playerToBan = core.getWrappedServer().getPlayer(uuidToBan);
        if (playerToBan != null) {
            core.getWrappedServer().getScheduler().runSyncNow(() -> playerToBan.ban(source, reason));
        }

        // Create the message to send out.
        final HammerText[] msg = getBanMessage(ban.getBannedUUID(), ban.getStaffUUID(), ban.getReason(), ban.getTempBanExpiration() != null, ban.getServerId() == null, ban.isPermanent(), conn);

        // Do we tell the server, or just the notified?
        if (flag.contains(BanFlagEnum.NOISY) || (!flag.contains(BanFlagEnum.QUIET) && cn.getNode("notifyAllOnBan").getBoolean())) {
            for (HammerText t : msg) {
                server.sendMessageToServer(t);
            }
        } else {
            server.sendMessageToPermissionGroup(
                    new HammerTextBuilder().add("[Hammer] " + messageBundle.getString("hammer.ban.quiet"), HammerTextColours.RED).build(),
                    HammerPermissions.notify);
            for (HammerText t : msg) {
                server.sendMessageToPermissionGroup(t, HammerPermissions.notify);
            }
        }

        if (cn.getNode("audit", "database").getBoolean() || cn.getNode("audit", "flatfile").getBoolean()) {
            createAuditLog(ban, conn);
        }

        return true;
    }

    /**
     * Performs any action specific to this ban type.
     * 
     * Note that the provided iterator needs to have the next method called on it to get the first usable argument.
     * Do not advance the iterator at the end.
     *
     * @param builder The {@link HammerCreateBanBuilder} to update.
     * @param argumentMap The arguments to pass to the method
     * @return <code>true</code> to signify success.
     */
    protected abstract boolean performSpecificActions(HammerCreateBanBuilder builder, ArgumentMap argumentMap);

    protected abstract BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, HammerCreateBanBuilder pendingBan) throws HammerException;

    protected String createReason(ArgumentMap argumentMap, List<String> otherReasons) {
        Optional<String> reas = argumentMap.<String>getArgument("reason");
        if (!reas.isPresent()) {
            return null;
        }

        if (!core.getConfig().getConfig().getNode("appendBanReasons").getBoolean(true)) {
            return reas.get();
        }

        StringBuilder sb = new StringBuilder(reas.get());
        if (otherReasons != null) {
            for (String reasons : otherReasons) {
                sb.append(" - ").append(reasons);
            }
        }

        return sb.toString();
    }

    private void createAuditLog(HammerCreateBan.Player cpb, DatabaseConnection conn) {
        int id = core.getConfig().getConfig().getNode("server", "id").getInt();

        try {
            AuditEntry ae = new AuditEntry(cpb.getStaffUUID(), cpb.getBannedUUID(), id, new Date(), ActionEnum.BAN, createAuditMessage(cpb, conn));
            insertAuditEntry(ae, conn);
        } catch (HammerException e) {
            core.getWrappedServer().getLogger().warn("Could not add audit entry.");
            e.printStackTrace();
        }
    }

    private String createAuditMessage(HammerCreateBan.Player cpb, DatabaseConnection conn) throws HammerException {
        String playerName = getName(cpb.getBannedUUID(), conn);
        String name;
        if (cpb.getStaffUUID().equals(HammerConstants.consoleUUID)) {
            name = String.format("*%s*", messageBundle.getString("hammer.console"));
        } else {
            name = getName(cpb.getStaffUUID(), conn);
        }

        String modifier = "";
        if (cpb.getTempBanExpiration() != null) {
            modifier = " " + MessageFormat.format(messageBundle.getString("hammer.audit.ban.temp"), dateFormatter.format(cpb.getTempBanExpiration()));
        } else if (cpb.isPermanent()) {
            modifier = " " + messageBundle.getString("hammer.permanently");
        }

        String fromAll = "";
        if (cpb.getServerId() == null) {
            fromAll = " " + messageBundle.getString("hammer.fromallservers");
        }

        return MessageFormat.format(messageBundle.getString("hammer.audit.ban"), playerName, modifier, name, fromAll, cpb.getReason());
    }

    private HammerText[] getBanMessage(UUID banned, UUID bannedBy, String reason, boolean isTemp, boolean isAll, boolean isPerm, DatabaseConnection conn) throws HammerException {
        String playerName = getName(banned, conn);

        HammerText[] messages = new HammerText[2];
        String name;
        if (bannedBy.equals(HammerConstants.consoleUUID)) {
            name = String.format("*%s*", messageBundle.getString("hammer.console"));
        } else {
            name = getName(bannedBy, conn);
        }

        String modifier = "";
        if (isTemp) {
            modifier = " " + messageBundle.getString("hammer.temporarily");
        } else if (isPerm) {
            modifier = " " + messageBundle.getString("hammer.permanently");
        }

        String fromAll = "";
        if (isAll) {
            fromAll = " " + messageBundle.getString("hammer.fromallservers");
        }

        HammerTextBuilder htb = new HammerTextBuilder();
        htb.add(HammerConstants.textTag + " ", HammerTextColours.RED)
                .add(playerName, HammerTextColours.WHITE)
                .add(" " + MessageFormat.format(messageBundle.getString("hammer.ban.banMessage"), modifier, fromAll), HammerTextColours.RED)
                .add(" " + name, HammerTextColours.WHITE);

        messages[0] = htb.build();

        htb.clear();

        htb.add(HammerConstants.textTag + " " + messageBundle.getString("hammer.reason") + " " + reason, HammerTextColours.RED);

        messages[1] = htb.build();
        return messages;
    }

    protected enum BanStatus
    {
        CONTINUE,
        NO_ACTION,
        TO_PERM,
        TO_GLOBAL
    }

    protected class BanInfo {
        final List<String> reasons;
        final BanStatus status;

        BanInfo(BanStatus status, List<String> reasons) {
            this.status = status;
            this.reasons = reasons;
        }
    }
}
