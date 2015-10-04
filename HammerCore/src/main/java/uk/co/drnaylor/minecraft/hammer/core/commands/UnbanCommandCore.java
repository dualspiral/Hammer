package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerPermissions;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

public class UnbanCommandCore extends CommandCore {

    private static final Pattern all = Pattern.compile("^-p?ap?$", Pattern.CASE_INSENSITIVE);
    private static final Pattern perm = Pattern.compile("^-a?pa?$", Pattern.CASE_INSENSITIVE);
    
    public UnbanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.unban.normal");
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
    protected boolean executeCommand(WrappedCommandSource source, List<String> arguments, DatabaseConnection conn) throws HammerException {
        try {
            if (arguments.isEmpty()) {
                sendUsageMessage(source);
                return true;
            }

            // Make a copy.
            List<String> copy = new ArrayList<>(arguments);

            // Last argument, player.
            String playerName = copy.remove(arguments.size() - 1);
            Set<UUID> uuids = new HashSet<>();

            // Get the player to unban if we can.
            UUID unban = null;
            WrappedPlayer player = core.getWrappedServer().getPlayer(playerName);
            if (player != null) {
                unban = player.getUUID();
            }

            // If we don't have them on this server...
            if (unban == null) {
                // ...do we have them in the Hammer DB?
                List<HammerPlayerInfo> players = conn.getPlayerHandler().getPlayersByName(playerName);
                for (HammerPlayerInfo p : players) {
                    uuids.add(p.getUUID());
                }
            } else {
                // Otherwise, we want them in the set.
                uuids.add(unban);
            }

            // No users, throw them out
            if (uuids.isEmpty()) {
                sendNoPlayerMessage(source, playerName);
                return true;
            }

            // Check flags.
            boolean allFlag = false;
            boolean permFlag = false;
            if (!copy.isEmpty()) {
                for (String c : copy) {
                    if (all.matcher(c).matches() && !allFlag) {
                        // Check for permission
                        if (source.hasPermission(HammerPermissions.globalUnban)) {
                            allFlag = true;
                        } else {
                            sendNoPermsMessage(source);
                            return true;
                        }
                    } else if (perm.matcher(c).matches() && !permFlag) {
                        if (source.hasPermission(HammerPermissions.permUnban)) {
                            permFlag = true;
                        } else {
                            sendNoPermsMessage(source);
                            return true;
                        }
                    } else {
                        sendUsageMessage(source);
                        return true;
                    }
                }
            }

            // If we have a ban...
            UUID bannee = null;
            int serverId =  core.getWrappedServer().getConfiguration().getConfigIntegerValue("server", "id");
            boolean ban = false;
            for (UUID u : uuids) {
                 HammerPlayerBan ban2 = conn.getBanHandler().getPlayerBanForServer(u, serverId);
                 if (ban2 == null) {
                     continue;
                 }

                 ban = true;
                 if (bannee != null && !bannee.equals(ban2.getBannedUUID())) {
                     // OK, if this is the case, then we need to tell the user of this problem...
                     sendTemplatedMessage(source, "hammer.unban.ambiguous", true, true, playerName);
                     return true;
                 } else {
                     if (ban2.isPermBan() && !permFlag) {
                         sendTemplatedMessage(source, "hammer.unban.permanent", true, true);
                         return true;
                     }

                     if (ban2.getServerId() == null && !allFlag) {
                         sendTemplatedMessage(source, "hammer.unban.allservers", true, true);
                         return true;
                     }

                     bannee = ban2.getBannedUUID();
                 }
            }

            // You can't unban those who aren't banned!
            if (!ban) {
                sendTemplatedMessage(source, "hammer.unban.noban", true, true, playerName);
                return true;
            }

            // If you get here, then we have a ban to undo!
            conn.getBanHandler().unbanFromServer(bannee, serverId);
            if (player != null) {
                player.unban();
                playerName = player.getName();
            }

            // Unban from all servers if that's needed.
            if (allFlag) {
                conn.getBanHandler().unbanFromAllServers(bannee);
            }

            // Unbanned. Tell the notified.
            sendUnbanMessage(playerName, source, allFlag);
            return true;
        } catch (Exception ex) {
            throw new HammerException("Command failed to execute", ex);
        } 
    }

    @Override
    public HammerText getUsageMessage() {
        return new HammerTextBuilder().add("/unban [-a] [-p] player", HammerTextColours.YELLOW).build();
    }

    private void sendUnbanMessage(String playerName, WrappedCommandSource source, boolean allFlag) {
        HammerTextBuilder htb = new HammerTextBuilder();
        htb.add(playerName + " ", HammerTextColours.WHITE);
        if (allFlag) {
            htb.add(messageBundle.getString("hammer.unban.unbanAllServers"), HammerTextColours.GREEN);
        } else {
            htb.add(messageBundle.getString("hammer.unban.unbanOneServer"), HammerTextColours.GREEN);
        }

        htb.add(" " + source.getName(), HammerTextColours.WHITE);
        core.getWrappedServer().sendMessageToPermissionGroup(htb.build(), HammerPermissions.notify);
    }
}
