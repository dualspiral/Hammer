package uk.co.drnaylor.minecraft.hammer.sponge.listeners;

import org.spongepowered.api.event.entity.player.PlayerJoinEvent;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;

public class PlayerJoinListener {

    private final HammerSponge hammerSponge;

    public PlayerJoinListener(HammerSponge hammerSponge) {
        this.hammerSponge = hammerSponge;
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        hammerSponge.addPlayerToRunnable(event.getUser());
    }
}
