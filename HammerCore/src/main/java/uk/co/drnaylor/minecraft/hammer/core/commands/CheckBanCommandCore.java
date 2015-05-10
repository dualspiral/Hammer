package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IMessageSender;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerMessageBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;

public class CheckBanCommandCore extends CommandCore {

    public CheckBanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.checkban");
    }

    @Override
    protected boolean executeCommand(UUID playerUUID, List<String> arguments, boolean isConsole, DatabaseConnection conn) throws HammerException {
        try {    
            IConfigurationProvider cp = core.getActionProvider().getConfigurationProvider();
            IMessageSender sender = core.getActionProvider().getMessageSender();

            if (arguments.size() != 1) {
                this.sendUsageMessage(playerUUID);
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
                sendNoPlayerMessage(playerUUID, playerName);
                return true;
            }

            if (uuids.size() > 1) {
                sendTemplatedMessage(playerUUID, "hammer.player.multiple", false, true, playerName);
                sendMessage(playerUUID, "------------------", false, false);
            }

            for (UUID uuid : uuids) {
                List<HammerPlayerBan> bans = conn.getBanHandler().getPlayerBans(uuid);
                if (uuids.size() > 1) {
                    sendMessage(playerUUID, "UUID: " + uuid.toString(), false, true);
                }

                if (bans.isEmpty()) {
                    sendTemplatedMessage(playerUUID, "hammer.player.check.nobans", false, true, playerName);
                } else {
                    sendTemplatedMessage(playerUUID, "hammer.player.check.bans", false, true, playerName, String.valueOf(bans.size()));
                    for (HammerPlayerBan b : bans) {
                        sendBanReason(b, playerUUID);
                    }
                }
            }

            return true;
        } catch (Exception ex) {
            throw new HammerException("An error occurred", ex);
        }
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
    }

    @Override
    public HammerText getUsageMessage() {
        return new HammerTextBuilder().add("/checkban <name>", HammerTextColours.YELLOW).build();
    }

    private void sendBanReason(HammerPlayerBan ban, UUID playerUUID) {
        sendMessage(playerUUID, "------------------", false, false);

        String server = ban.getServerId() == null ? messageBundle.getString("hammer.player.check.allservers") :
                MessageFormat.format(messageBundle.getString("hammer.player.check.serverid"), ban.getServerId().toString());

        String modifier = "";
        if (ban.isPermBan()) {
            modifier = String.format(" %s ", messageBundle.getString("hammer.player.check.perm"));
        } else if (ban.isTempBan()) {
            modifier = String.format(" %s ", MessageFormat.format("hammer.player.check.temp",
                    core.createTimeStringFromOffset(ban.getDateOfUnban().getTime() - (new Date()).getTime())));
        }

        sendTemplatedMessage(playerUUID, "hammer.player.check.from", false, false, server, modifier);
        sendTemplatedMessage(playerUUID, "hammer.player.check.banned", false, false, dateFormatter.format(ban.getDateOfBan()));
        sendTemplatedMessage(playerUUID, "hammer.player.check.bannedby", false, false, ban.getBanningStaffName());
        sendTemplatedMessage(playerUUID, "hammer.player.check.reason", false, false, ban.getReason());
    }
}
