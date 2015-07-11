package uk.co.drnaylor.minecraft.hammer.core.wrappers;

import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayer;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

public interface WrappedPlayer extends WrappedCommandSource {

    /**
     * Bans the player with the specified reason
     *
     * @param source The {@link WrappedCommandSource} that performed this ban
     * @param reason The reason
     */
    void ban(WrappedCommandSource source, HammerText reason);

    /**
     * Bans the player with the specified reason
     *
     * @param source The {@link WrappedCommandSource} that performed this ban
     * @param reason The reason
     */
    void ban(WrappedCommandSource source, String reason);

    /**
     * Unbans the player
     */
    void unban();

    /**
     * Gets whether the player is banned.
     *
     * @return Whether the player is banned or not.
     */
    boolean isBanned();

    /**
     * Kicks the player with the specified reason.
     *
     * @param reason The reason.
     */
    void kick(HammerText reason);

    /**
     * Kicks the player with the specified reason.
     *
     * @param reason The reason.
     */
    void kick(String reason);

    /**
     * Gets the {@link HammerPlayer} that represents this player.
     *
     * @return The {@link HammerPlayer}
     */
    HammerPlayer getHammerPlayer();
}
