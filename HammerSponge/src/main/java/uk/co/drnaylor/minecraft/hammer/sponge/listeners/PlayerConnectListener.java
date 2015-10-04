package uk.co.drnaylor.minecraft.hammer.sponge.listeners;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.service.user.UserStorage;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerConnectListenerCore;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.sponge.wrappers.SpongeWrappedPlayer;

public class PlayerConnectListener {

    private final Game game;
    private final PlayerConnectListenerCore eventCore;
    private final Logger logger;
    private UserStorage storageService = null;

    public PlayerConnectListener(Logger logger, Game game, PlayerConnectListenerCore eventCore) {
        this.logger = logger;
        this.game = game;
        this.eventCore = eventCore;
    }

    private void getServices() {
        if (storageService == null) {
            storageService = game.getServiceManager().provide(UserStorage.class).get();
        }
    }

    /**
     * Runs when a player has been authenticated with the Mojang services.
     *
     * @param event The event to fire.
     */
    @Listener
    public void onPlayerConnection(ClientConnectionEvent.Auth event) {
        getServices();
        try {
            GameProfile pl = event.getProfile();
            String host = event.getConnection().getAddress().getAddress().getHostAddress();

            User user = storageService.getOrCreate(pl);
            HammerText text = eventCore.handleEvent(
                    new SpongeWrappedPlayer(game, user),
                    host);

            if (text != null) {
                event.setCancelled(true);
                event.setMessage(HammerTextConverter.constructMessage(text));
            }
        } catch (HammerException e) {
            logger.error("Connection to the MySQL database failed. Falling back to the Minecraft ban list.");
            e.printStackTrace();
        }
    }
}
