package uk.co.drnaylor.minecraft.hammer.core.wrappers;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

import java.util.UUID;

public interface WrappedPlayer {

    /**
     * Gets the name of the player.
     *
     * @return The name.
     */
    String getName();

    /**
     * Gets the Unique Identifier of the player.
     *
     * @return The {@link UUID}
     */
    UUID getUUID();

    /**
     * Sends a message to the player
     *
     * @param message The message
     */
    void sendMessage(HammerText message);

    /**
     * Sends a message to the player
     *
     * @param message The message
     */
    void sendMessage(String message);

    /**
     * Gets whether the player has the specified permission
     *
     * @param permission The permission
     * @return <code>true</code> if the player has the permission specified.
     */
    boolean hasPermission(String permission);

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
