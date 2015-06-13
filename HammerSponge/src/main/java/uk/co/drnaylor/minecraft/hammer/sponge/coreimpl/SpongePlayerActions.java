package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerActions;

import java.util.UUID;

public class SpongePlayerActions implements IPlayerActions {
    @Override
    @Deprecated
    public void kickPlayer(UUID player, UUID kicker, String reason) {

    }

    @Override
    public void kickAllPlayers(UUID kicker, String reason) {

    }

    @Override
    public void banPlayer(UUID player, UUID banner, String reason) {

    }

    @Override
    public void unbanPlayer(UUID player) {

    }
}
