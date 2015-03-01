package uk.co.drnaylor.minecraft.hammer.core.interfaces;

import java.util.UUID;

/**
 * Builds messages and sends them to the requested player.
 */
public interface IPlayerMessageBuilder {

    void sendNoPermsMessage(UUID uuid);

    void sendNoPlayerMessage(UUID uuid);

    void sendAlreadyBannedMessage(UUID uuid);

    void sendUsageMessage(UUID uuid, String message);

    void sendErrorMessage(UUID uuid, String message);

    void sendStandardMessage(UUID player, String message, boolean withTag);

    void sendAlreadyBannedFailMessage(UUID uuidToBan);

    void sendToPermMessage(UUID playerUUID);

    void sendToAllMessage(UUID playerUUID);
}
