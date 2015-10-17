package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.text.MessageFormat;
import java.util.*;

import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerPermissions;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.BanFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedConfiguration;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

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

        WrappedConfiguration cp = core.getWrappedServer().getConfiguration();
        HammerCreatePlayerBanBuilder builder = new HammerCreatePlayerBanBuilder(source.getUUID(), cp.getConfigIntegerValue("server", "id"), cp.getConfigStringValue("server", "name"));

        // Next up, the player. Can we find them? Get the last player...
        UUID uuidToBan = arguments.<List<HammerPlayerInfo>>getArgument("player").get().stream().sorted(Collections.reverseOrder()).findFirst().get().getUUID();
        Optional<List<BanFlagEnum>> flags = arguments.<List<BanFlagEnum>>getArgument("flags");
        List<BanFlagEnum> flag;
        if (flags.isPresent()) {
            flag = flags.get();
        } else {
            flag = Collections.emptyList();
        }

        // Start a transaction. We might need to delete some rows here.
        conn.startTransaction();
        BanInfo status = checkOtherBans(uuidToBan, conn, flag.contains(BanFlagEnum.ALL));

        // Set global flag here, before the checks below.
        builder.setAll(flag.contains(BanFlagEnum.ALL));

        if (status.status == BanStatus.NO_ACTION) {
            sendTemplatedMessage(source, "hammer.player.alreadyBanned", true, true);
            return true;
        } else if (status.status == BanStatus.TO_PERM) {
            // Auto upgrade! If you get a global ban, and a permanent ban is already in force, the 
            // permanent ban takes effect everywhere.
            sendTemplatedMessage(source, "hammer.player.upgradeToPerm", false, false);
            builder.setPerm(true);
        } else if (status.status == BanStatus.TO_GLOBAL) {
            // Auto upgrade! If you get a perm ban, and a global ban is already in force, the 
            // permanent ban takes effect everywhere.
            sendTemplatedMessage(source, "hammer.player.upgradeToAll", false, false);
            conn.getBanHandler().unbanFromAllServers(uuidToBan);
            builder.setAll(true);
        }

        builder.setPlayerToBan(uuidToBan);

        if (!performSpecificActions(builder, arguments)) {
            // Usage.
            sendUsageMessage(source);
            conn.rollbackTransaction();
            return true;
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

        HammerCreatePlayerBan ban = builder.build();
        conn.getBanHandler().createServerBan(ban);

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
        if (flag.contains(BanFlagEnum.NOISY) || (!flag.contains(BanFlagEnum.QUIET) && server.getConfiguration().getConfigBooleanValue("notifyAllOnBan"))) {
            for (HammerText t : msg) {
                server.sendMessageToServer(t);
            }
        } else {
            server.sendMessageToPermissionGroup(
                    new HammerTextBuilder().add("[Hammer] This ban is quiet. Only those with notify permissions will see this.", HammerTextColours.RED).build(),
                    HammerPermissions.notify);
            for (HammerText t : msg) {
                server.sendMessageToPermissionGroup(t, HammerPermissions.notify);
            }
        }

        return true;
    }

    /**
     * Performs any action specific to this ban type.
     * 
     * Note that the provided iterator needs to have the next method called on it to get the first usable argument.
     * Do not advance the iterator at the end.
     *
     * @param builder The {@link HammerCreatePlayerBanBuilder} to update.
     * @param argumentMap The arguments to pass to the method
     * @return <code>true</code> to signify success.
     */
    protected abstract boolean performSpecificActions(HammerCreatePlayerBanBuilder builder, ArgumentMap argumentMap);

    protected abstract BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, boolean isGlobal) throws HammerException;

    protected String createReason(ArgumentMap argumentMap, List<String> otherReasons) {
        Optional<String> reas = argumentMap.<String>getArgument("reason");
        if (!reas.isPresent()) {
            return null;
        }

        StringBuilder sb = new StringBuilder(reas.get());
        if (otherReasons != null) {
            for (String reasons : otherReasons) {
                sb.append(" - ").append(reasons);
            }
        }

        return sb.toString();
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

    private String getName(UUID name, DatabaseConnection conn) throws HammerException {
        WrappedPlayer playerTarget = core.getWrappedServer().getPlayer(name);
        String playerName;
        if (playerTarget == null) {
            // Do we have them in the Hammer DB?
            HammerPlayerInfo p = conn.getPlayerHandler().getPlayer(name);
            if (p == null) {
                playerName = "Unknown Player";
            } else {
                playerName = p.getName();
            }
        } else {
            playerName = playerTarget.getName();
        }

        return playerName;
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
