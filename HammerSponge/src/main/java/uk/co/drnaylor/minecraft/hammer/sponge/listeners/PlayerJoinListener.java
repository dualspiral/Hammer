package uk.co.drnaylor.minecraft.hammer.sponge.listeners;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerJoinListenerCore;
import uk.co.drnaylor.minecraft.hammer.sponge.wrappers.SpongeWrappedPlayer;

import java.util.Optional;

public class PlayerJoinListener {

    private final Game game;
    private final PlayerJoinListenerCore core;

    public PlayerJoinListener(Game game, PlayerJoinListenerCore core) {
        this.game = game;
        this.core = core;
    }


    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Optional<Player> pl = event.getTargetEntity().getPlayer();
        if (pl.isPresent()) {
            core.handleEvent(new SpongeWrappedPlayer(game, pl.get()));
        }
    }
}
