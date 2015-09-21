package uk.co.drnaylor.minecraft.hammer.core.listenercores;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerIPBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.util.Date;
import java.util.UUID;

/**
 * Provides methods and logic for player connection events that is API agnostic
 */
public class PlayerConnectListenerCore {

    private final HammerCore core;

    public PlayerConnectListenerCore(HammerCore core) {
        this.core = core;
    }

    public HammerText handleEvent(WrappedPlayer player, String hostAddress) throws HammerException {
        HammerBan ban = getBan(player.getHammerPlayer().getUUID(), hostAddress);
        if (ban == null && player.isBanned()) {
            player.unban();
            return null;
        }

        // Set their ban on the server too - in case Hammer goes down.
        if (ban instanceof HammerPlayerBan && !player.isBanned()) {
            player.ban(core.getWrappedServer().getConsole(), ban.getReason());
        } else if (ban instanceof HammerIPBan && player.isBanned()) {
            player.unban();
        }

        return constructBanMessage(ban);
    }

    /**
     * Gets any ban information
     *
     * @param player The {@link UUID} of the player to check
     * @param hostAddress The IP address of the player to check
     * @return The ban, or <code>null</code>
     */
    public HammerBan getBan(UUID player, String hostAddress) throws HammerException {
        // Get the server ID.
        int serverId = core.getWrappedServer().getConfiguration().getConfigIntegerValue("server", "id");

        try (DatabaseConnection conn = core.getDatabaseConnection()) {
            HammerPlayerBan ban = conn.getBanHandler().getPlayerBanForServer(player, serverId);
            if (ban != null) {
                return ban;
            }

            HammerIPBan ipban = conn.getBanHandler().getIpBan(hostAddress);
            return ipban;
        } catch (Exception ex) {
            throw new HammerException("Connection to the MySQL database failed. Falling back to the Minecraft ban list.", ex);
        }
    }

    public HammerText constructBanMessage(HammerBan ban) {
        String name = ban.getBanningStaffName();
        if (name == null) {
            name = "Unknown";
        }

        HammerTextBuilder htb = new HammerTextBuilder();
        StringBuilder sb = new StringBuilder();

        if (ban instanceof HammerPlayerBan) {
            if (ban.isTempBan()) {
                sb.append("You have been temporarily banned. You may rejoin in ");
                sb.append(core.createTimeStringFromOffset(ban.getDateOfUnban().getTime() - new Date().getTime()));
            } else if (((HammerPlayerBan)ban).isPermBan()) {
                sb.append("You have been banned with no right of appeal!");
            } else {
                sb.append("You have been banned!");
            }
        } else if (ban instanceof HammerIPBan) {
            if (ban.isTempBan()) {
                sb.append("You have been temporarily IP banned!");
            } else {
                sb.append("You have been IP banned!");
            }
        }

        sb.append("\n");
        htb.add(sb.toString(), HammerTextColours.RED);
        htb.add("---------\nBanned by: ", HammerTextColours.GRAY);
        htb.add(name + "\n", HammerTextColours.BLUE);
        htb.add("Reason: ", HammerTextColours.GRAY);
        htb.add(ban.getReason(), HammerTextColours.BLUE);
        return htb.build();
    }
}
