package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayer;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerPermissionCheck;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;

public class UnbanCommandCore extends CommandCore {

    private static Pattern all = Pattern.compile("^-p?ap?$", Pattern.CASE_INSENSITIVE);
    private static Pattern perm = Pattern.compile("^-a?pa?$", Pattern.CASE_INSENSITIVE);
    
    public UnbanCommandCore(HammerCore core) {
        super(core);
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
    }

    /**
     * Executes the unban command. Expecting /unban [-p] [-a] player.
     * 
     * <p>
     * This command will only unban global and/or perm bans with the correct permission, and the correct flag. For an all server
     * perm ban, either -pa, -ap, -p -a or -a -p will suffice - but both permissions must be enabled.
     * </p>
     * 
     * @param playerUUID
     * @param arguments
     * @param isConsole
     * @return
     * @throws HammerException 
     */
    @Override
    protected boolean executeCommand(UUID playerUUID, List<String> arguments, boolean isConsole, DatabaseConnection conn) throws HammerException {
        try {
            IPlayerPermissionCheck permsCheck = core.getActionProvider().getPermissionCheck();
            if (arguments.isEmpty()) {
                sendUsage(playerUUID);
                return true;
            }

            // Make a copy.
            List<String> copy = new ArrayList<>(arguments);

            // Last argument, player.
            String playerName = copy.remove(arguments.size() - 1);
            Set<UUID> uuids = new HashSet<>();
            UUID unban = core.getActionProvider().getPlayerTranslator().playerNameToUUID(playerName);
            if (unban == null) {
                // Do we have them in the Hammer DB?
                List<HammerPlayer> players = conn.getPlayerHandler().getPlayersByName(playerName);
                for (HammerPlayer p : players) {
                    uuids.add(p.getUUID());
                }
            } else {
                uuids.add(unban);
            }

            if (uuids.isEmpty()) {
                sendNoPlayerMessage(playerUUID, playerName);
                return true;
            }

            boolean allFlag = false;
            boolean permFlag = false;
            if (!copy.isEmpty()) {
                // Check flags.
                for (String c : copy) {
                    if (all.matcher(c).matches() && !allFlag) {
                        if (permsCheck.hasPermissionToUnbanFromAllServers(playerUUID)) {
                            allFlag = true;
                        } else {
                            sendNoPermsMessage(playerUUID);
                            return true;
                        }
                    } else if (perm.matcher(c).matches() && !permFlag) {
                        if (permsCheck.hasPermissionToUnbanPermanent(playerUUID)) {
                            permFlag = true;
                        } else {
                            sendNoPermsMessage(playerUUID);
                            return true;
                        }
                    } else {
                        sendUsage(playerUUID);
                        return true;
                    }
                }
            }

            // If we have a ban...
            UUID bannee = null;
            int serverId =  core.getActionProvider().getConfigurationProvider().getServerId();
            boolean ban = false;
            for (UUID u : uuids) {
                 HammerPlayerBan ban2 = conn.getBanHandler().getPlayerBanForServer(u, serverId);
                 if (ban2 == null) {
                     continue;
                 }

                 ban = true;
                 if (bannee != null && !bannee.equals(ban2.getBannedUUID())) {
                     // OK, if this is the case, then we need to tell the user of this problem...
                     sendTemplatedMessage(playerUUID, "hammer.unban.ambiguous", true, true, playerName);
                     return true;
                 } else {
                     if (ban2.isPermBan() && !permFlag) {
                         sendTemplatedMessage(playerUUID, "hammer.unban.permanent", true, true);
                         return true;
                     }

                     if (ban2.getServerId() == null && !allFlag) {
                         sendTemplatedMessage(playerUUID, "hammer.unban.allservers", true, true);
                         return true;
                     }

                     bannee = ban2.getBannedUUID();
                 }
            }

            if (!ban) {
                sendTemplatedMessage(playerUUID, "hammer.unban.noban", true, true, playerName);
                return true;
            }

            // If you get here, then we have a ban to undo!
            conn.getBanHandler().unbanFromServer(bannee, serverId);
            core.getActionProvider().getPlayerActions().unbanPlayer(bannee);
            if (allFlag) {
                conn.getBanHandler().unbanFromAllServers(bannee);
            }

            // Unbanned. Tell the notified.
            sendUnbanMessage(bannee, playerName, playerUUID, allFlag);
            return true;
        } catch (Exception ex) {
            throw new HammerException("Command failed to execute", ex);
        } 
    }

    private void sendUsage(UUID playerUUID) {
        sendUsageMessage(playerUUID, "/unban [-a] [-p] player");
    }

    private void sendUnbanMessage(UUID bannee, String playerName, UUID bannedBy, boolean allFlag) {
        String name;
        if (bannedBy.equals(HammerConstants.consoleUUID)) {
            name = String.format("*%s*", messageBundle.getString("hammer.console"));
        } else {
            name = core.getActionProvider().getPlayerTranslator().uuidToPlayerName(bannee);
        }

        String plName = core.getActionProvider().getPlayerTranslator().uuidToPlayerName(bannee);;
        if (plName == null || plName == "") {
            plName = playerName;
        }

        HammerTextBuilder htb = new HammerTextBuilder();
        htb.addText(name, HammerTextColours.WHITE);
        if (allFlag) {
            htb.addText(messageBundle.getString("hammer.unban.unbanAllServers"), HammerTextColours.GREEN);
        } else {
            htb.addText(messageBundle.getString("hammer.unban.unbanOneServer"), HammerTextColours.GREEN);
        }

        htb.addText(" " + plName, HammerTextColours.WHITE);
        core.getActionProvider().getMessageSender().sendMessageToPlayersWithPermission("hammer.notify", htb.build());
    }
}
