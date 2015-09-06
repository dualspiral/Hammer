package uk.co.drnaylor.minecraft.hammer.sponge.listeners;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.user.UserStorage;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.Bans;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerConnectListenerCore;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

import java.util.Collection;
import java.util.UUID;

public class PlayerConnectListener {

    private final Game game;
    private final PlayerConnectListenerCore eventCore;
    private final Logger logger;
    private BanService service = null;
    private UserStorage storageService = null;

    public PlayerConnectListener(Logger logger, Game game, PlayerConnectListenerCore eventCore) {
        this.logger = logger;
        this.game = game;
        this.eventCore = eventCore;
    }

    private void getServices() {
        if (service == null || storageService == null) {
            service = game.getServiceManager().provide(BanService.class).get();
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
            UUID uuid = pl.getUniqueId();
            String host = event.getConnection().getAddress().getAddress().getHostAddress();

            User user = storageService.getOrCreate(pl);
            HammerBan ban = eventCore.getBan(uuid, host);
            if (ban == null) {
                if (service.isBanned(user)) {
                    service.pardon(user);
                }

                return;
            }

            // Set their ban on the server too - in case Hammer goes down.
            if (ban instanceof HammerPlayerBan) {
                BanService service = game.getServiceManager().provide(BanService.class).get();
                Collection<Ban.User> bans = service.getBansFor(user);

                if (bans.isEmpty()) {
                    service.ban(Bans.of(user, Texts.of(ban.getReason())));
                }
            }

            event.setCancelled(true);
            event.setMessage(HammerTextConverter.constructMessage(eventCore.constructBanMessage(ban)));
        } catch (HammerException e) {
            logger.error("Connection to the MySQL database failed. Falling back to the Minecraft ban list.");
            e.printStackTrace();
        }
    }
}
