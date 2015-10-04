package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.util.ban.BanBuilder;
import org.spongepowered.api.util.ban.BanType;
import org.spongepowered.api.util.ban.Bans;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

import java.net.InetSocketAddress;
import java.util.Optional;
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
    public void ban(WrappedCommandSource source, HammerText reason) {
        ban(source, reason.toString());
    }

    /**
     * Bans the player with the specified reason
     *
     * @param reason The reason
     */
    @Override
    public void ban(WrappedCommandSource source, String reason) {
        Optional<BanService> serviceOptional = getBanService();
        if (serviceOptional.isPresent()) {
            BanBuilder builder = Bans.builder().reason(Texts.of(reason)).user(player).type(BanType.USER_BAN);
            if (source instanceof SpongeWrappedPlayer) {
                Optional<Player> sourceplayer = ((SpongeWrappedPlayer) source).getSpongePlayer();
                if (sourceplayer.isPresent()) {
                    builder.source(sourceplayer.get());
                }
            } else if (source instanceof SpongeWrappedConsole) {
                builder.source(((SpongeWrappedConsole) source).getSpongeSource());
            }

            serviceOptional.get().ban(builder.build());
        }
    }

    /**
     * Unbans the player
     */
    @Override
    public void unban() {
        Optional<BanService> serviceOptional = getBanService();
        if (serviceOptional.isPresent()) {
            serviceOptional.get().pardon(player);
        }
    }

    /**
     * Gets whether the player is banned.
     *
     * @return Whether the player is banned or not.
     */
    @Override
    public boolean isBanned() {
        Optional<BanService> serviceOptional = getBanService();
        if (serviceOptional.isPresent()) {
            return serviceOptional.get().isBanned(player);
        } else {
            // No ban service means that bans aren't likely to materialise...
            return false;
        }
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

    @Override
    public HammerPlayerInfo getHammerPlayer() {
        Optional<Player> pl = player.getPlayer();
        if (pl.isPresent()) {
            InetSocketAddress addr = player.getPlayer().get().getConnection().getAddress();
            String ip = addr.toString().substring(1).split(":")[0];
            return new HammerPlayerInfo(player.getUniqueId(), player.getName(), ip);
        } else {
            try (DatabaseConnection c = HammerSponge.getInstance().getCore().getDatabaseConnection()) {
                return c.getPlayerHandler().getPlayer(player.getUniqueId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Gets the {@link BanService}
     *
     * @return The {@link BanService}
     */
    private Optional<BanService> getBanService() {
        return game.getServiceManager().provide(BanService.class);
    }

    /**
     * Gets the Sponge {@link User}
     *
     * @return The {@link User}
     */
    public User getSpongeUser() {
        return player;
    }

    private Optional<Player> getSpongePlayer() {
        return player.getPlayer();
    }
}
