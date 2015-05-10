package uk.co.drnaylor.minecraft.hammer.sponge.listeners;

import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.net.PlayerConnectionEvent;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;

import java.util.UUID;

public class PlayerConnectListener {

    private final HammerSponge plugin;

    public PlayerConnectListener(HammerSponge plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onPlayerConnection(PlayerConnectionEvent event) {
        UUID playerId = event.getConnection().getPlayer().getUniqueId();

    }
}
