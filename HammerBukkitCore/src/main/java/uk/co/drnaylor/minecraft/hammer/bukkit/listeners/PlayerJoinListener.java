package uk.co.drnaylor.minecraft.hammer.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;

public class PlayerJoinListener implements Listener {
    private final HammerBukkitPlugin plugin;

    public PlayerJoinListener(HammerBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs when the player joins. Adds the user to the database.
     * 
     * @param e 
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        this.plugin.addToHammerPlayerRunnable(e.getPlayer());
    }
}
