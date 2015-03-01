package uk.co.drnaylor.minecraft.hammer.core.interfaces;

import java.util.UUID;

public interface IPlayerToUUIDTranslator {
    UUID playerNameToUUID(String name);

    String uuidToPlayerName(UUID uuid);
}
