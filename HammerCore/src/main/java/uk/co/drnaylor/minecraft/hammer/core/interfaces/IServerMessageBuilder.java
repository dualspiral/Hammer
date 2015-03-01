package uk.co.drnaylor.minecraft.hammer.core.interfaces;

import java.util.UUID;

public interface IServerMessageBuilder {

    void sendBanMessageToNotified(UUID banned, UUID bannedBy, String reason, boolean isTemp, boolean isAll, boolean isPerm);

    void sendBanMessageToAll(UUID banned, UUID bannedBy, String reason, boolean isTemp, boolean isAll, boolean isPerm);

    void sendUnbanMessageToNotified(UUID bannee, UUID playerUUID, boolean allFlag);
}
