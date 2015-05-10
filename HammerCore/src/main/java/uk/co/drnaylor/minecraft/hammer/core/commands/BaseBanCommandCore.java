package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IConfigurationProvider;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.PlayerPermissionCheckBase;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;

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
        PlayerPermissionCheckBase check = core.getActionProvider().getPermissionCheck();
        Iterator<String> argumentIterator = arguments.iterator();

        if (arguments.size() < minArguments()) {
            sendUsageMessage(playerUUID);
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
                    sendNoPermsMessage(playerUUID);
                    return true;
                }

                builder.setAll(true);
            }

            currentArg = argumentIterator.next();
        }

        // Next up, the player
        UUID uuidToBan = core.getActionProvider().getPlayerTranslator().playerNameToUUID(currentArg);
        if (uuidToBan == null) {
            sendNoPlayerMessage(playerUUID, currentArg);
            return true;
        }

        // Start a transaction. We might need to delete some rows here.
        conn.startTransaction();
        BanInfo status = checkOtherBans(uuidToBan, conn, isGlobal);
        if (status.status == BanStatus.NO_ACTION) {
            sendTemplatedMessage(playerUUID, "hammer.player.alreadyBanned", true, true);

            return true;
        } else if (status.status == BanStatus.TO_PERM) {
            // Auto upgrade! If you get a global ban, and a permanent ban is already in force, the 
            // permanent ban takes effect everywhere.
            sendTemplatedMessage(playerUUID, "hammer.player.upgradeToPerm", false, false);
            builder.setPerm(true);
        } else if (status.status == BanStatus.TO_GLOBAL) {
            // Auto upgrade! If you get a perm ban, and a global ban is already in force, the 
            // permanent ban takes effect everywhere.
            sendTemplatedMessage(playerUUID, "hammer.player.upgradeToAll", false, false);
            conn.getBanHandler().unbanFromAllServers(uuidToBan);
            builder.setAll(true);
        }

        builder.setPlayerToBan(uuidToBan);

        if (!performSpecificActions(builder, argumentIterator)) {
            // Usage.
            sendUsageMessage(playerUUID);
            conn.rollbackTransaction();
            return true;
        }

        String reason = createReason(argumentIterator, status.reasons);
        if (reason == null) {
            // Usage.
            sendUsageMessage(playerUUID);
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

        HammerText[] msg = getBanMessage(ban.getBannedUUID(), ban.getStaffUUID(), ban.getReason(), ban.getTempBanExpiration() != null, ban.getServerId() != null, ban.isPermanent());

        // Do we tell the server, or just the notified?
        if (!isQuiet && core.getActionProvider().getConfigurationProvider().notifyServerOfBans()) {
            for (HammerText t : msg) {
                core.getActionProvider().getMessageSender().sendMessageToAllPlayers(t);
            }
        } else {
            for (HammerText t : msg) {
                core.getActionProvider().getMessageSender().sendMessageToPlayersWithPermission("hammer.notify", t);
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
     * @param builder
     * @param argumentIterator
     * @return 
     */
    protected abstract boolean performSpecificActions(HammerCreatePlayerBanBuilder builder, Iterator<String> argumentIterator);

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

    /**
     * Gets the constructed set of {@link HammerTextBuilder}
     *
     * @param banned
     * @param bannedBy
     * @param reason
     * @param isTemp
     * @param isAll
     * @param isPerm
     * @return
     */
    protected HammerText[] getBanMessage(UUID banned, UUID bannedBy, String reason, boolean isTemp, boolean isAll, boolean isPerm) {
        String playerName = core.getActionProvider().getPlayerTranslator().uuidToPlayerName(banned);

        HammerText[] messages = new HammerText[2];
        String name;
        if (bannedBy.equals(HammerConstants.consoleUUID)) {
            name = String.format("*%s*", messageBundle.getString("hammer.console"));
        } else {
            name = core.getActionProvider().getPlayerTranslator().uuidToPlayerName(bannedBy);
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
        htb.add(HammerConstants.textTag, HammerTextColours.RED)
                .add(playerName, HammerTextColours.WHITE)
                .add(" " + MessageFormat.format(messageBundle.getString("hammer.ban.banMessage"), modifier, fromAll), HammerTextColours.RED)
                .add(" " + name, HammerTextColours.WHITE);

        messages[0] = htb.build();

        htb.clear();

        StringBuilder sb = new StringBuilder(HammerConstants.textTag).append(" ")
                .append(messageBundle.getString("hammer.reason")).append(" ")
                .append(reason);
        htb.add(sb.toString(), HammerTextColours.RED);

        messages[1] = htb.build();
        return messages;
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
        protected final List<String> reasons;
        protected final BanStatus status;

        protected BanInfo(BanStatus status, List<String> reasons) {
            this.status = status;
            this.reasons = reasons;
        }
    }
}
