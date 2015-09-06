package uk.co.drnaylor.minecraft.hammer.sponge.listeners;

import com.google.common.base.Optional;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;

public class PlayerJoinListener {

    private final HammerSponge hammerSponge;

    public PlayerJoinListener(HammerSponge hammerSponge) {
        this.hammerSponge = hammerSponge;
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Optional<Player> pl = event.getTargetEntity().getPlayer();
        if (pl.isPresent()) {
            hammerSponge.addPlayerToRunnable(pl.get());
        }
    }
}
