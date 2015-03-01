package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IConfigurationProvider;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerMessageBuilder;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerPermissionCheck;

public abstract class BaseBanCommandCore extends CommandCore {

    private final Pattern flagPattern = Pattern.compile("^-[ag]$");
    private final Pattern quietPattern = Pattern.compile("^-[q]$");

    public BaseBanCommandCore(HammerCore core) {
        super(core);
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
    }

    /**
     * Gets the minimum number of arguments for the command.
     * 
     * @return 
     */
    protected abstract int minArguments();

    /**
     * Executes the specific command. Code here is common to all player bans
     * 
     * @param playerUUID The UUID of the player executing this command.
     * @param arguments The arguments. Expecting 2+. [-a] player reason
     * @param isConsole If the executor is the console, this will be true. The UUID will also be all zeros.
     * @return <code>true</code> if the command does the expected task. <code>false</code> if the specific 
     *         implementation command handler needs to take over.
     * 
     * @throws HammerException Thrown in place of all other exceptions.
     */
    @Override
    public final boolean executeCommand(UUID playerUUID, List<String> arguments, boolean isConsole, DatabaseConnection conn) throws HammerException {
        IConfigurationProvider cp = core.getActionProvider().getConfigurationProvider();
        IPlayerPermissionCheck check = core.getActionProvider().getPermissionCheck();
        IPlayerMessageBuilder playerMsg = core.getActionProvider().getPlayerMessageBuilder();
        Iterator<String> argumentIterator = arguments.iterator();

        if (arguments.size() < minArguments()) {
            playerMsg.sendUsageMessage(playerUUID, getUsage());
            return true;
        }

        String currentArg = argumentIterator.next();

        HammerCreatePlayerBanBuilder builder = new HammerCreatePlayerBanBuilder(playerUUID, cp.getServerId(), cp.getServerName());

        // First argument may indicate a ban for all.
        boolean isGlobal = false;
        boolean isQuiet = false;
        while (currentArg.startsWith("-")) {
            if (!isQuiet) {
                isQuiet = this.isQuietBan(currentArg);
            }

            if (!isGlobal && this.isGlobalBan(currentArg)) {
                isGlobal = true;
                if (!check.hasPermissionToBanOnAllServers(playerUUID)) {
                    playerMsg.sendNoPermsMessage(playerUUID);
                    return true;
                }

                builder.setAll(true);
            }

            currentArg = argumentIterator.next();
        }

        // Next up, the player
        UUID uuidToBan = core.getActionProvider().getPlayerTranslator().playerNameToUUID(currentArg);
        if (uuidToBan == null) {
            core.getActionProvider().getPlayerMessageBuilder().sendNoPlayerMessage(playerUUID);
            return true;
        }

        // Start a transaction. We might need to delete some rows here.
        conn.startTransaction();
        BanInfo status = checkOtherBans(uuidToBan, conn, isGlobal);
        if (status.status == BanStatus.NO_ACTION) {
            core.getActionProvider().getPlayerMessageBuilder().sendAlreadyBannedMessage(playerUUID);

            return true;
        } else if (status.status == BanStatus.TO_PERM) {
            // Auto upgrade! If you get a global ban, and a permanent ban is already in force, the 
            // permanent ban takes effect everywhere.
            core.getActionProvider().getPlayerMessageBuilder().sendToPermMessage(playerUUID);
            builder.setPerm(true);
        } else if (status.status == BanStatus.TO_GLOBAL) {
            // Auto upgrade! If you get a perm ban, and a global ban is already in force, the 
            // permanent ban takes effect everywhere.
            core.getActionProvider().getPlayerMessageBuilder().sendToAllMessage(playerUUID);
            conn.getBanHandler().unbanFromAllServers(uuidToBan);
            builder.setAll(true);
        }

        builder.setPlayerToBan(uuidToBan);

        if (!performSpecificActions(builder, argumentIterator)) {
            // Usage.
            playerMsg.sendUsageMessage(playerUUID, getUsage());
            conn.rollbackTransaction();
            return true;
        }

        String reason = createReason(argumentIterator, status.reasons);
        if (reason == null) {
            // Usage.
            playerMsg.sendUsageMessage(playerUUID, getUsage());
            conn.rollbackTransaction();
            return true;
        }

        builder.setReason(reason);
        builder.setExternalId(conn.getNewExternalID());

        HammerCreatePlayerBan ban = builder.build();
        conn.getBanHandler().createServerBan(ban);

        // Commit the transaction
        conn.commitTransaction();

        core.getActionProvider().getPlayerActions().banPlayer(ban.getBannedUUID(), ban.getStaffUUID(), reason);

        // Do we tell the server, or just the notified?
        if (!isQuiet && core.getActionProvider().getConfigurationProvider().notifyServerOfBans()) {
            core.getActionProvider().getServerMessageBuilder().sendBanMessageToAll(ban.getBannedUUID(), ban.getStaffUUID(), ban.getReason(), ban.getTempBanExpiration() != null, ban.getServerId() != null, ban.isPermanent());
        } else {
            core.getActionProvider().getServerMessageBuilder().sendBanMessageToNotified(ban.getBannedUUID(), ban.getStaffUUID(), ban.getReason(), ban.getTempBanExpiration() != null, ban.getServerId() != null, ban.isPermanent());
        }

        return true;
    }

    /**
     * Performs any action specific to this ban type.
     * 
     * Note that the provided iterator needs to have the next method called on it to get the first usable argument.
     * Do not advance the iterator at the end.
     * 
     * @param builder
     * @param argumentIterator
     * @return 
     */
    protected abstract boolean performSpecificActions(HammerCreatePlayerBanBuilder builder, Iterator<String> argumentIterator);

    /**
     * Gets and returns the command usage.
     * 
     * @return The command usage.
     */
    protected abstract String getUsage();

    protected abstract BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, boolean isGlobal) throws HammerException; 

    protected String createReason(Iterator<String> argumentIterator, List<String> otherReasons) {
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

    protected enum BanStatus
    {
        CONTINUE(""),
        NO_ACTION(""),
        TO_PERM("The ban was upgraded to a permanent ban, due to one being in force on this or another server."),
        TO_GLOBAL("The ban was upgraded to a global ban, due to one being in force on this or another server.");
        
        private final String msg;
        private BanStatus(String msg) {
            this.msg = msg;
        }

        protected String getMsg() {
            return msg;
        }
    }

    protected class BanInfo {
        protected final List<String> reasons;
        protected final BanStatus status;

        protected BanInfo(BanStatus status, List<String> reasons) {
            this.status = status;
            this.reasons = reasons;
        }
    }
}
