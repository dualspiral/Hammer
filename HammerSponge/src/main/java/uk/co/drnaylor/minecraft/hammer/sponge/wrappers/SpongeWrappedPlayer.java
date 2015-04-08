package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.util.UUID;

public class SpongeWrappedPlayer implements WrappedPlayer {
    /**
     * Gets the name of the player.
     *
     * @return The name.
     */
    @Override
    public String getName() {
        return null;
    }

    /**
     * Gets the Unique Identifier of the player.
     *
     * @return The {@link UUID}
     */
    @Override
    public UUID getUUID() {
        return null;
    }

    /**
     * Kicks a player with the specified reason.
     *
     * @param reason The reason.
     */
    @Override
    public void kickPlayer(HammerText reason) {

    }

    /**
     * Kicks a player with the specified reason.
     *
     * @param reason The reason.
     */
    @Override
    public void kickPlayer(String reason) {

    }
}
