package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import uk.co.drnaylor.minecraft.hammer.core.interfaces.IServerMessageBuilder;

import java.util.UUID;

public class SpongeServerMessageBuilder implements IServerMessageBuilder {
    @Override
    public void sendBanMessageToNotified(UUID banned, UUID bannedBy, String reason, boolean isTemp, boolean isAll, boolean isPerm) {

    }

    @Override
    public void sendBanMessageToAll(UUID banned, UUID bannedBy, String reason, boolean isTemp, boolean isAll, boolean isPerm) {

    }

    @Override
    public void sendUnbanMessageToNotified(UUID bannee, UUID playerUUID, boolean allFlag) {

    }
}
