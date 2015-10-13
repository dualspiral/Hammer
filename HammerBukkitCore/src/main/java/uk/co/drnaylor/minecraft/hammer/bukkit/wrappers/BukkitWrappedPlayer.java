package uk.co.drnaylor.minecraft.hammer.bukkit.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;
import uk.co.drnaylor.minecraft.hammer.bukkit.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Wraps a Bukkit {@link org.bukkit.entity.Player}
 */
public final class BukkitWrappedPlayer implements WrappedPlayer {

    private final OfflinePlayer player;

    public BukkitWrappedPlayer(OfflinePlayer player) {
        this.player = player;
    }

    public static BukkitWrappedPlayer of(UUID uuid) {
        OfflinePlayer pl = Bukkit.getServer().getOfflinePlayer(uuid);
        if (pl.hasPlayedBefore()) {
            return new BukkitWrappedPlayer(pl);
        }

        return null;
    }

    public static BukkitWrappedPlayer ofOnlinePlayer(String player) {
        Player pl = Bukkit.getPlayerExact(player);
        if (pl != null) {
            return new BukkitWrappedPlayer(pl);
        }

        return null;
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
        sendMessage(HammerTextConverter.constructMessage(message));
    }

    /**
     * Sends a message to the player
     *
     * @param message The message
     */
    @Override
    public void sendMessage(String message) {
        if (player.isOnline()) {
            player.getPlayer().sendMessage(message);
        }
    }

    /**
     * Gets whether the player has the specified permission
     *
     * @param permission The permission
     * @return <code>true</code> if the player has the permission specified.
     */
    @Override
    public boolean hasPermission(String permission) {
        return player.isOnline() && player.getPlayer().hasPermission(permission);

    }

    /**
     * Bans a player with the specified reason
     *
     * @param source The {@link WrappedCommandSource} that performed this ban
     * @param reason The reason
     */
    @Override
    public void ban(WrappedCommandSource source, HammerText reason) {
        ban(source, HammerTextConverter.constructMessage(reason));
    }

    /**
     * Bans a player with the specified reason
     *
     * @param reason The reason
     */
    @Override
    public void ban(WrappedCommandSource source, String reason) {
        player.setBanned(true);

        kick("Banned by: " + source.getName() + "\n" + "Reason: " + reason);
    }

    /**
     * Unbans the player
     */
    @Override
    public void unban() {
        player.setBanned(false);
    }

    /**
     * Gets whether the player is banned.
     *
     * @return Whether the player is banned or not.
     */
    @Override
    public boolean isBanned() {
        return player.isBanned();
    }

    /**
     * Kicks the player
     *
     * @param text The reason
     */
    @Override
    public void kick(HammerText text) {
        kick(HammerTextConverter.constructMessage(text));
    }

    /**
     * Kicks the player
     *
     * @param reason The reason.
     */
    @Override
    public void kick(String reason) {
        if (player.isOnline()) {
            player.getPlayer().kickPlayer(ChatColor.translateAlternateColorCodes('&', reason));
        }
    }

    @Override
    public boolean isOnline() {
        return player.isOnline();
    }

    @Override
    public HammerPlayerInfo getHammerPlayer() {
        if (player.isOnline()) {
            InetSocketAddress addr = player.getPlayer().getAddress();
            String ip = addr != null ? addr.toString().substring(1).split(":")[0] : "127.0.0.1";
            return new HammerPlayerInfo(player.getUniqueId(), player.getName(), ip);
        } else {
            try (DatabaseConnection c = HammerBukkitPlugin.getPlugin().getHammerCore().getDatabaseConnection()) {
                return c.getPlayerHandler().getPlayer(player.getUniqueId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
