package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerToUUIDTranslator;

import java.util.UUID;

public class SpongePlayerToUUIDTranslator implements IPlayerToUUIDTranslator {
    @Override
    public UUID playerNameToUUID(String name) {
        return null;
    }

    @Override
    public String uuidToPlayerName(UUID uuid) {
        return null;
    }
}
