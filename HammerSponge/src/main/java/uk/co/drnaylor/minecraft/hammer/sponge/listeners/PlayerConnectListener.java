package uk.co.drnaylor.minecraft.hammer.sponge.listeners;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.network.PlayerConnectionEvent;
import org.spongepowered.api.service.ban.BanService;
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

    public PlayerConnectListener(Logger logger, Game game, PlayerConnectListenerCore eventCore) {
        this.logger = logger;
        this.game = game;
        this.eventCore = eventCore;
    }

    @Subscribe
    public void onPlayerConnection(PlayerConnectionEvent event) {
        try {
            Player pl = event.getConnection().getPlayer();
            UUID uuid = pl.getUniqueId();
            String host = event.getConnection().getAddress().getAddress().getHostAddress();

            HammerBan ban = eventCore.getBan(uuid, host);
            if (ban == null) {
                Optional<BanService> service = game.getServiceManager().provide(BanService.class);
                service.get().pardon(pl);
                return;
            }

            // Set their ban on the server too - in case Hammer goes down.
            if (ban instanceof HammerPlayerBan) {
                BanService service = game.getServiceManager().provide(BanService.class).get();
                Collection<Ban.User> bans = service.getBansFor(pl);

                if (bans.isEmpty()) {
                    service.ban(Bans.of(pl, Texts.of(ban.getReason())));
                }
            }

            event.getConnection().getPlayer().kick(HammerTextConverter.constructMessage(eventCore.constructBanMessage(ban)));
        } catch (HammerException e) {
            logger.error("Connection to the MySQL database failed. Falling back to the Minecraft ban list.");
            e.printStackTrace();
        }
    }
}
