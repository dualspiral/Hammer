package uk.co.drnaylor.minecraft.hammer.core.interfaces;

import java.util.UUID;

public interface IPlayerActions {
    /**
     * Kicks a player
     *
     * @param player The player to kick
     * @param kicker The player doing the kicking
     * @param reason The reason
     *
     * @deprecated Use the {@link uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer}
     * instead.
     */
    @Deprecated
    void kickPlayer(UUID player, UUID kicker, String reason);

    void kickAllPlayers(UUID kicker, String reason);

    void banPlayer(UUID player, UUID banner, String reason);

    void unbanPlayer(UUID player);
}
