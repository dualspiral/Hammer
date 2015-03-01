package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayer;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IConfigurationProvider;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerMessageBuilder;

public class CheckBanCommandCore extends CommandCore {

    public CheckBanCommandCore(HammerCore core) {
        super(core);
    }

    @Override
    protected boolean executeCommand(UUID playerUUID, List<String> arguments, boolean isConsole, DatabaseConnection conn) throws HammerException {
        try {    
            IConfigurationProvider cp = core.getActionProvider().getConfigurationProvider();
            IPlayerMessageBuilder playerMsg = core.getActionProvider().getPlayerMessageBuilder();

            if (arguments.size() != 1) {
                playerMsg.sendUsageMessage(playerUUID, "/checkban <player>");
                return true;
            }

            // Get the player out.
            String playerName = arguments.get(0);
            Set<UUID> uuids = new HashSet<>();
            UUID u = core.getActionProvider().getPlayerTranslator().playerNameToUUID(playerName);
            if (u == null) {
                // Do we have them in the Hammer DB?
                List<HammerPlayer> players = conn.getPlayerHandler().getPlayersByName(playerName);
                for (HammerPlayer p : players) {
                    uuids.add(p.getUUID());
                }
            } else {
                uuids.add(u);
            }

            if (uuids.isEmpty()) {
                playerMsg.sendNoPlayerMessage(playerUUID);
                return true;
            }

            if (uuids.size() > 1) {
                playerMsg.sendStandardMessage(playerUUID, "There are multiple players called " + playerName + " they are listed below with their UUIDs.", true);
                playerMsg.sendStandardMessage(playerUUID, "------------------", false);
            }

            for (UUID uuid : uuids) {
                List<HammerPlayerBan> bans = conn.getBanHandler().getPlayerBans(uuid);
                if (uuids.size() > 1) {
                    playerMsg.sendStandardMessage(playerUUID, "UUID: " + uuid.toString(), true);
                }

                if (bans.isEmpty()) {
                    playerMsg.sendStandardMessage(playerUUID, "Player " + playerName + " has no bans on record.", true);
                } else {
                    playerMsg.sendStandardMessage(playerUUID, "Player " + playerName + " has " + bans.size() + " ban(s) on record.", true);
                    for (HammerPlayerBan b : bans) {
                        sendBanReason(b, playerUUID, playerMsg);
                    }
                }
            }

            return true;
        } catch (Exception ex) {
            throw new HammerException("An error occured", ex);
        }
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
    }

    private void sendBanReason(HammerPlayerBan ban, UUID playerUUID, IPlayerMessageBuilder msg) {
        msg.sendStandardMessage(playerUUID, "------------------", false);
        StringBuilder sb = new StringBuilder("Banned from ");
        if (ban.getServerId() == null) {
            sb.append("all servers ");
        } else {
            sb.append("server ID ").append(ban.getServerId());
        }

        if (ban.isPermBan()) {
            sb.append("permanently ");
        } else if (ban.isTempBan()) {
            sb.append(" for ").append(core.createTimeStringFromOffset(ban.getDateOfUnban().getTime() - (new Date()).getTime()));
        }

        msg.sendStandardMessage(playerUUID, sb.toString(), false);
        msg.sendStandardMessage(playerUUID, "Banned by: " + ban.getBanningStaffName(), false);
        msg.sendStandardMessage(playerUUID, "Reason: " + ban.getReason(), false);
    }
}
