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
     * Kicks a player with the specified reason.
     *
     * @param reason The reason.
     */
    void kickPlayer(HammerText reason);

    /**
     * Kicks a player with the specified reason.
     *
     * @param reason The reason.
     */
    void kickPlayer(String reason);
}
