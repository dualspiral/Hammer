package uk.co.drnaylor.minecraft.hammer.core.interfaces;

import java.util.UUID;

public interface IPlayerActions {
    void kickPlayer(UUID player, UUID kicker, String reason);

    void banPlayer(UUID player, UUID banner, String reason);

    void unbanPlayer(UUID player);
}
