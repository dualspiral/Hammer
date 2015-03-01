package uk.co.drnaylor.minecraft.hammer.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.co.drnaylor.minecraft.hammer.spigot.HammerSpigot;

public class PlayerJoinListener implements Listener {
    private final HammerSpigot plugin;

    public PlayerJoinListener(HammerSpigot plugin) {
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
