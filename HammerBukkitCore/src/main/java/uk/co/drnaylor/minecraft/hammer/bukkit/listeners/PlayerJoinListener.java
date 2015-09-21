package uk.co.drnaylor.minecraft.hammer.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;
import uk.co.drnaylor.minecraft.hammer.bukkit.wrappers.BukkitWrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerJoinListenerCore;
import uk.co.drnaylor.minecraft.hammer.core.runnables.HammerPlayerUpdateRunnable;

public class PlayerJoinListener implements Listener {

    private final PlayerJoinListenerCore eventCore;

    public PlayerJoinListener(PlayerJoinListenerCore eventCore) {
        this.eventCore = eventCore;
    }

    /**
     * Runs when the player joins. Adds the user to the database.
     * 
     * @param e The event to handle.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        eventCore.handleEvent(new BukkitWrappedPlayer(e.getPlayer()));
    }
}
