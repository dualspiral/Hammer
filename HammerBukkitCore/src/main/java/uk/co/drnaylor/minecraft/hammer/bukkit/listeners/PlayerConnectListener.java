package uk.co.drnaylor.minecraft.hammer.bukkit.listeners;

import java.util.Date;
import java.util.UUID;

import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerIPBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

public class PlayerConnectListener implements Listener {

    private final HammerBukkitPlugin plugin;

    public PlayerConnectListener(HammerBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerConnect(AsyncPlayerPreLoginEvent event) {
        try (DatabaseConnection conn = plugin.getHammerCore().getDatabaseConnection()) {
            UUID uuid = event.getUniqueId();
            HammerPlayerBan ban = conn.getBanHandler().getPlayerBanForServer(uuid, plugin.getConfig().getInt("server.id"));
            if (ban != null) {
                // Set their ban on the server too - in case Hammer goes down.
                plugin.getServer().getOfflinePlayer(uuid).setBanned(true);
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, constructBanMessage(ban));
                return;
            }
            
            // If we get here, the player was unbanned, or not banned. We won't get here if the connection
            // fails.
            plugin.getServer().getOfflinePlayer(uuid).setBanned(false);
            HammerIPBan ipban = conn.getBanHandler().getIpBan(event.getAddress().getHostAddress());
            if (ipban != null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, constructIpBanMessage(ipban));
            }
        } catch (Exception ex) {
            plugin.getLogger().severe("Connection to the MySQL database failed. Falling back to the Minecraft ban list.");
            ex.printStackTrace();
        }
    }

    private String constructBanMessage(HammerPlayerBan ban) {
        OfflinePlayer pl = Bukkit.getServer().getOfflinePlayer(ban.getBanningStaff());
        
        String name = pl.getName();
        if (name == null) {
            if (ban.getBanningStaffName() != null) {
                name = ban.getBanningStaffName();
            } else {
                name = "Unknown";
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.RED);
        
        if (ban.isTempBan()) {
            sb.append("You have been temporarily banned. You may rejoin in ");
            sb.append(plugin.getHammerCore().createTimeStringFromOffset(ban.getDateOfUnban().getTime() - new Date().getTime()));
        } else if (ban.isPermBan()) {
            sb.append("You have been banned with no right of appeal!");
        } else {
            sb.append("You have been banned!");
        }

        sb.append("\n");
        sb.append(ChatColor.GRAY).append("---------").append("\n");
        sb.append("Banned by: ").append(ChatColor.BLUE).append(name).append("\n");
        sb.append(ChatColor.GRAY).append("Reason: ").append(ChatColor.BLUE).append(ban.getReason());
        return sb.toString();
    }

    private String constructIpBanMessage(HammerIPBan ban) {
        OfflinePlayer pl = Bukkit.getServer().getOfflinePlayer(ban.getBanningStaff());

        String name = pl.getName();
        if (name == null) {
            if (ban.getBanningStaffName() != null) {
                name = ban.getBanningStaffName();
            } else {
                name = "Unknown";
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.RED);
        
        if (ban.isTempBan()) {
            sb.append("You have been temporarily IP banned!");
        } else {
            sb.append("You have been IP banned!");
        }

        sb.append("\n");
        sb.append(ChatColor.GRAY).append("---------").append("\n");
        sb.append("Banned by: ").append(ChatColor.BLUE).append(name).append("\n");
        sb.append(ChatColor.GRAY).append("Reason: ").append(ChatColor.BLUE).append(ban.getReason());
        return sb.toString();
    }
}
