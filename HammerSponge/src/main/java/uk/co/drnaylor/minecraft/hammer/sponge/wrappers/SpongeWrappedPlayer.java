package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import com.google.common.base.Optional;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.User;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanFactory;
import org.spongepowered.api.util.ban.Bans;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

import java.util.Collection;
import java.util.UUID;

public class SpongeWrappedPlayer implements WrappedPlayer {

    private final User player;
    private final Game game;

    public SpongeWrappedPlayer(Game game, User player) {
        this.player = player;
        this.game = game;
    }

    /**
     * Gets the name of the player.
     *
     * @return The name.
     */
    @Override
    public String getName() {
        return player.getName();
    }

    /**
     * Gets the Unique Identifier of the player.
     *
     * @return The {@link UUID}
     */
    @Override
    public UUID getUUID() {
        return player.getUniqueId();
    }

    /**
     * Sends a message to the player
     *
     * @param message The message
     */
    @Override
    public void sendMessage(HammerText message) {
        Optional<Player> onlinePlayer = player.getPlayer();
        if (onlinePlayer.isPresent()) {
            onlinePlayer.get().sendMessage(HammerTextConverter.constructMessage(message));
        }

    }

    /**
     * Sends a message to the player
     *
     * @param message The message
     */
    @Override
    public void sendMessage(String message) {
        Optional<Player> onlinePlayer = player.getPlayer();
        if (onlinePlayer.isPresent()) {
            onlinePlayer.get().sendMessage(ChatTypes.SYSTEM, Texts.of(message));
        }
    }

    /**
     * Gets whether the player has the specified permission.
     *
     * @param permission The permission
     * @return <code>true</code> if the player has the permission specified.
     */
    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    /**
     * Bans the player with the specified reason
     *
     * @param reason The reason
     */
    @Override
    public void ban(HammerText reason) {
        ban(reason.toString());
    }

    /**
     * Bans the player with the specified reason
     *
     * @param reason The reason
     */
    @Override
    public void ban(String reason) {
        Ban ban = Bans.of(player, Texts.of(reason));
        getBanService().ban(ban);
    }

    /**
     * Unbans the player
     */
    @Override
    public void unban() {
        getBanService().pardon(player);
    }

    /**
     * Gets whether the player is banned.
     *
     * @return Whether the player is banned or not.
     */
    @Override
    public boolean isBanned() {
        BanService service = getBanService();
        return service.isBanned(player);
    }

    /**
     * Kicks a player with the specified reason.
     *
     * @param reason The reason.
     */
    @Override
    public void kick(HammerText reason) {
        Optional<Player> onlinePlayer = player.getPlayer();
        if (onlinePlayer.isPresent()) {
            onlinePlayer.get().kick(HammerTextConverter.constructMessage(reason));
        }
    }

    /**
     * Kicks a player with the specified reason.
     *
     * @param reason The reason.
     */
    @Override
    public void kick(String reason) {
        Optional<Player> onlinePlayer = player.getPlayer();
        if (onlinePlayer.isPresent()) {
            onlinePlayer.get().kick(Texts.of(reason));
        }
    }

    /**
     * Gets the {@link BanService}
     *
     * @return The {@link BanService}
     */
    private BanService getBanService() {
        return game.getServiceManager().provide(BanService.class).get();
    }
}
