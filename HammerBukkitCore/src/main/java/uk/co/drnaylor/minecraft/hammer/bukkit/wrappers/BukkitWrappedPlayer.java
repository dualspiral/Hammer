package uk.co.drnaylor.minecraft.hammer.bukkit.wrappers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.bukkit.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.util.UUID;

/**
 * Wraps a Bukkit {@link org.bukkit.entity.Player}
 */
public final class BukkitWrappedPlayer implements WrappedPlayer {

    private final OfflinePlayer player;

    private BukkitWrappedPlayer(OfflinePlayer player) {
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
     * Kicks the player
     *
     * @param text The reason
     */
    @Override
    public void kickPlayer(HammerText text) {
        kickPlayer(HammerTextConverter.constructMessage(text));
    }

    /**
     * Kicks the player
     *
     * @param reason The reason.
     */
    @Override
    public void kickPlayer(String reason) {
        if (player.isOnline()) {
            player.getPlayer().kickPlayer(reason);
        }
    }
}
