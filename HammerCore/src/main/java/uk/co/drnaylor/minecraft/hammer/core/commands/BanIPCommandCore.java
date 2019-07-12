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

import com.google.common.collect.Lists;
import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerPermissions;
import uk.co.drnaylor.minecraft.hammer.core.audit.ActionEnum;
import uk.co.drnaylor.minecraft.hammer.core.audit.AuditEntry;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.BanIPFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.*;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreateBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreateBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RunAsync
public class BanIPCommandCore extends CommandCore {

    public BanIPCommandCore(HammerCore core) {
        super(core);
        permissionNodes.add("hammer.ipban.normal");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        return Lists.newArrayList(
            new ParserEntry("banflags", new FlagParser<>(BanIPFlagEnum.class), true),
            new ParserEntry("ip", new IP4Parser(), true),
            new ParserEntry("player", new HammerPlayerParser(core), true),
            new ParserEntry("reason", new StringParser(true), false)
        );
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
    }

    /**
     * Executes the specific routines in this command core with the specified source.
     *
     * @param source    The {@link WrappedCommandSource} that is executing the command.
     * @param arguments The arguments of the command
     * @param conn      If the command requires database access, holds a {@link DatabaseConnection} object. Otherwise, null.
     * @return Whether the command succeeded
     * @throws HammerException Thrown if an exception is thrown in the command core.
     */
    @Override
    protected boolean executeCommand(WrappedCommandSource source, ArgumentMap arguments, DatabaseConnection conn) throws HammerException {
        // Check to see if we have an IP. If we do, we can ignore the player.
        InetAddress addr;
        if (arguments.containsKey("ip")) {
            addr = arguments.<InetAddress>getArgument("ip").get();
        } else if (arguments.containsKey("player")) {
            try {
                addr = InetAddress.getByName(arguments.<HammerPlayerInfo>getArgument("player").get().getIp());
            } catch (UnknownHostException e) {
                throw new HammerException("Player IP address is not valid.", e);
            }
        } else {
            throw new HammerException("An IP address or a player name must be submitted.");
        }

        if (checkOtherBans(addr, conn)) {
            sendTemplatedMessage(source, "hammer.ip.alreadyBanned", true, true);
            return true;
        }

        // We have an IP to ban. We do not offer perm bans. We should offer global bans.
        List<BanIPFlagEnum> flags = arguments.containsKey("banflags") ? Lists.newArrayList() :
                arguments.<List<BanIPFlagEnum>>getArgument("banflags").get();

        if (flags.contains(BanIPFlagEnum.ALL) && source.hasPermission(HammerPermissions.ipBanGlobal)) {
            sendTemplatedMessage(source, "hammer.player.noperms", true, true);
            return true;
        }

        // Create the builder
        ConfigurationNode cn = core.getConfig().getConfig().getNode("server");
        HammerCreateBanBuilder builder = new HammerCreateBanBuilder(
                source.getUUID(), cn.getNode("id").getInt(), cn.getNode("name").getString());
        builder.setAll(flags.contains(BanIPFlagEnum.ALL));
        performTempbanActions(arguments, builder);
        String reason = arguments.<String>getArgument("reason").get();
        builder.setIPAddress(addr).setReason(reason);

        HammerCreateBan cb = builder.build();
        if (!(cb instanceof HammerCreateBan.IP)) {
            throw new HammerException("Could not build an IP ban");
        }

        HammerCreateBan.IP cbp = (HammerCreateBan.IP)cb;
        conn.getBanHandler().createIPBan(cbp);
        conn.commitTransaction();

        // Now, ban the IP in the vanilla manager!
        core.getWrappedServer().getScheduler().runSyncNow(() -> core.getWrappedServer().banIP(addr, reason));

        // IP Bans do not get a public audience, because sending IP addresses out is the wrong thing to do.
        core.getWrappedServer().sendMessageToPermissionGroup(
                new HammerTextBuilder().add("[Hammer] " + messageBundle.getString("hammer.ban.quiet"), HammerTextColours.RED).build(),
                HammerPermissions.notify);

        for (HammerText hammerText : getBanMessage(addr, source.getUUID(), reason, false, flags.contains(BanIPFlagEnum.ALL), conn)) {
            core.getWrappedServer().sendMessageToPermissionGroup(hammerText, HammerPermissions.notify);
        }

        if (cn.getNode("audit", "database").getBoolean() || cn.getNode("audit", "flatfile").getBoolean()) {
            createAuditLog(cbp, conn);
        }

        return true;
    }

    protected void performTempbanActions(ArgumentMap args, HammerCreateBanBuilder builder) {
    }

    @Override
    protected String commandName() {
        return "banip";
    }

    private boolean checkOtherBans(InetAddress addr, DatabaseConnection conn) throws HammerException {
        return !conn.getBanHandler().getIPBans(addr).isEmpty();
    }

    private HammerText[] getBanMessage(InetAddress banned, UUID bannedBy, String reason, boolean isTemp, boolean isAll, DatabaseConnection conn) throws HammerException {
        String ip = banned.getHostAddress();

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
        }

        String fromAll = "";
        if (isAll) {
            fromAll = " " + messageBundle.getString("hammer.fromallservers");
        }

        HammerTextBuilder htb = new HammerTextBuilder();
        htb.add(HammerConstants.textTag + " ", HammerTextColours.RED)
                .add(" " + MessageFormat.format(messageBundle.getString("hammer.ipban.banMessage"), ip, modifier, fromAll), HammerTextColours.RED)
                .add(" " + name, HammerTextColours.WHITE);

        messages[0] = htb.build();

        htb.clear();

        htb.add(HammerConstants.textTag + " " + messageBundle.getString("hammer.reason") + " " + reason, HammerTextColours.RED);

        messages[1] = htb.build();
        return messages;
    }

    private void createAuditLog(HammerCreateBan.IP cpb, DatabaseConnection conn) {
        int id = core.getServerId();

        try {
            AuditEntry ae = new AuditEntry(cpb.getStaffUUID(), null, id, new Date(), ActionEnum.BANIP, createAuditMessage(cpb, conn));
            insertAuditEntry(ae, conn);
        } catch (HammerException e) {
            core.getWrappedServer().getLogger().warn("Could not add audit entry.");
            e.printStackTrace();
        }
    }

    private String createAuditMessage(HammerCreateBan.IP cpb, DatabaseConnection conn) throws HammerException {
        String ip = cpb.getBannedIP().getHostAddress();
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

        return MessageFormat.format(messageBundle.getString("hammer.audit.ipban"), ip, modifier, name, fromAll, cpb.getReason());
    }
}
