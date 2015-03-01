package uk.co.drnaylor.minecraft.hammer.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkit;

public class PlayerJoinListener implements Listener {
    private final HammerBukkit plugin;

    public PlayerJoinListener(HammerBukkit plugin) {
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
