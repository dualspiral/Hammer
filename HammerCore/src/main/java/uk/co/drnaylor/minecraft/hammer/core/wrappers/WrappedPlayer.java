package uk.co.drnaylor.minecraft.hammer.core.wrappers;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

import java.util.UUID;

public interface WrappedPlayer extends WrappedCommandSource {

    /**
     * Bans the player with the specified reason
     *
     * @param reason The reason
     */
    void ban(HammerText reason);

    /**
     * Bans the player with the specified reason
     *
     * @param reason The reason
     */
    void ban(String reason);

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
}
