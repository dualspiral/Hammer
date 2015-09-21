package uk.co.drnaylor.minecraft.hammer.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;
import uk.co.drnaylor.minecraft.hammer.bukkit.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.bukkit.wrappers.BukkitWrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerConnectListenerCore;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

public class PlayerConnectListener implements Listener {

    private final HammerBukkitPlugin plugin;
    private final PlayerConnectListenerCore eventCore;

    public PlayerConnectListener(HammerBukkitPlugin plugin) {
        this.plugin = plugin;
        this.eventCore = new PlayerConnectListenerCore(plugin.getHammerCore());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerConnect(AsyncPlayerPreLoginEvent event) {
        try {
            HammerText text = eventCore.handleEvent(
                    new BukkitWrappedPlayer(Bukkit.getOfflinePlayer(event.getUniqueId())),
                    event.getAddress().getHostAddress());

            if (text != null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, HammerTextConverter.constructMessage(text));
            }
        } catch (HammerException e) {
            plugin.getLogger().severe("Connection to the MySQL database failed. Falling back to the Minecraft ban list.");
            e.printStackTrace();
        }
    }
}
