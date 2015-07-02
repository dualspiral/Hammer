package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerPermissions;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayer;
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

public abstract class BaseBanCommandCore extends CommandCore {

    private final Pattern flagPattern = Pattern.compile("^-[ag]$");
    private final Pattern quietPattern = Pattern.compile("^-[q]$");
    private final Pattern noisyPattern = Pattern.compile("^-[n]$");

    BaseBanCommandCore(HammerCore core) {
        super(core);
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
    }

    /**
     * Gets the minimum number of arguments for the command.
     * 
     * @return The minimum number of arguments to accept.
     */
    protected abstract int minArguments();

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
    public final boolean executeCommand(WrappedCommandSource source, List<String> arguments, DatabaseConnection conn) throws HammerException {
        WrappedServer server = core.getWrappedServer();

        Iterator<String> argumentIterator = arguments.iterator();

        // If we don't have enough arguments, the command is obviously malformed.
        if (arguments.size() < minArguments()) {
            sendUsageMessage(source);
            return true;
        }

        String currentArg = argumentIterator.next();

        WrappedConfiguration cp = core.getWrappedServer().getConfiguration();
        HammerCreatePlayerBanBuilder builder = new HammerCreatePlayerBanBuilder(source.getUUID(), cp.getConfigIntegerValue("server", "id"), cp.getConfigStringValue("server", "name"));

        // First argument may indicate a ban for all.
        boolean isGlobal = false;
        boolean isQuiet = false;
        boolean isNoisy = false;
        while (currentArg.startsWith("-")) {
            if (!isQuiet) {
                isQuiet = this.isQuietBan(currentArg);
            }

            if (!isNoisy) {
                isNoisy = this.isNoisyBan(currentArg);
            }

            if (!isGlobal && this.isGlobalBan(currentArg)) {
                isGlobal = true;
                if (!source.hasPermission(HammerPermissions.globalBan)) {
                    sendNoPermsMessage(source);
                    return true;
                }

                builder.setAll(true);
            }

            currentArg = argumentIterator.next();
        }

        // Next up, the player. Can we find them?
        UUID uuidToBan;
        WrappedPlayer playerToBan = server.getPlayer(currentArg);
        if (playerToBan != null) {
            uuidToBan = playerToBan.getUUID();
        } else {
            // We can't find them - but do they exist in Hammer?
            HammerPlayer players = null;
            try {
                players = conn.getPlayerHandler().getLastPlayerByName(currentArg);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (players == null) {
                // Nope.
                sendNoPlayerMessage(source, currentArg);
                return true;
            }

            // Yep!
            uuidToBan = players.getUUID();
        }

        // Start a transaction. We might need to delete some rows here.
        conn.startTransaction();
        BanInfo status = checkOtherBans(uuidToBan, conn, isGlobal);
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

        if (!performSpecificActions(builder, argumentIterator)) {
            // Usage.
            sendUsageMessage(source);
            conn.rollbackTransaction();
            return true;
        }

        String reason = createReason(argumentIterator, status.reasons);
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
        if (playerToBan != null) {
            playerToBan.ban(source, reason);
        }

        // Create the message to send out.
        HammerText[] msg = getBanMessage(ban.getBannedUUID(), ban.getStaffUUID(), ban.getReason(), ban.getTempBanExpiration() != null, ban.getServerId() == null, ban.isPermanent(), conn);

        // Do we tell the server, or just the notified?
        if (isNoisy || (!isQuiet && server.getConfiguration().getConfigBooleanValue("notifyAllOnBan"))) {
            for (HammerText t : msg) {
                server.sendMessageToServer(t);
            }
        } else {
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
     * @param argumentIterator The arguments to pass to the method
     * @return <code>true</code> to signify success.
     */
    protected abstract boolean performSpecificActions(HammerCreatePlayerBanBuilder builder, Iterator<String> argumentIterator);

    protected abstract BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, boolean isGlobal) throws HammerException;

    String createReason(Iterator<String> argumentIterator, List<String> otherReasons) {
        if (!argumentIterator.hasNext()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        while (argumentIterator.hasNext()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }

            sb.append(argumentIterator.next());
        }

        if (otherReasons != null) {
            for (String reasons : otherReasons) {
                sb.append(" - ").append(reasons);
            }
        }

        return sb.toString();
    }

    private boolean isGlobalBan(String argument0) {
        return flagPattern.matcher(argument0).matches();
    }

    private boolean isQuietBan(String argument0) {
        return quietPattern.matcher(argument0).matches();
    }

    private boolean isNoisyBan(String argument0) {
        return noisyPattern.matcher(argument0).matches();
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
            HammerPlayer p = conn.getPlayerHandler().getPlayer(name);
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
        CONTINUE(""),
        NO_ACTION(""),
        TO_PERM("The ban was upgraded to a permanent ban, due to one being in force on this or another server."),
        TO_GLOBAL("The ban was upgraded to a global ban, due to one being in force on this or another server.");
        
        private final String msg;
        BanStatus(String msg) {
            this.msg = msg;
        }

        protected String getMsg() {
            return msg;
        }
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
