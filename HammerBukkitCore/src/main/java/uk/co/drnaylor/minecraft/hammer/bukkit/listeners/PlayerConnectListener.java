package uk.co.drnaylor.minecraft.hammer.bukkit.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;
import uk.co.drnaylor.minecraft.hammer.bukkit.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerConnectListenerCore;

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
            HammerBan ban = eventCore.getBan(event.getUniqueId(), event.getAddress().getHostAddress());
            if (ban == null) {
                plugin.getServer().getOfflinePlayer(event.getUniqueId()).setBanned(false);
                return;
            }

            // Set their ban on the server too - in case Hammer goes down.
            plugin.getServer().getOfflinePlayer(event.getUniqueId()).setBanned((ban instanceof HammerPlayerBan));
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, HammerTextConverter.constructMessage(eventCore.constructBanMessage(ban)));
        } catch (HammerException e) {
            plugin.getLogger().severe("Connection to the MySQL database failed. Falling back to the Minecraft ban list.");
            e.printStackTrace();
        }
    }
}
