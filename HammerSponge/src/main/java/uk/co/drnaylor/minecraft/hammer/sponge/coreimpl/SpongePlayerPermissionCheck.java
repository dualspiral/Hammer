package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerPermissionCheck;

import java.util.UUID;

public class SpongePlayerPermissionCheck implements IPlayerPermissionCheck {
    @Override
    public boolean hasPermissionToBan(UUID player) {
        return false;
    }

    @Override
    public boolean hasPermissionToBanPermanent(UUID player) {
        return false;
    }

    @Override
    public boolean hasPermissionToBanOnAllServers(UUID player) {
        return false;
    }

    @Override
    public boolean hasPermissionToBanTemporarily(UUID player) {
        return false;
    }

    @Override
    public boolean hasPermissionToUnban(UUID player) {
        return false;
    }

    @Override
    public boolean hasPermissionToUnbanFromAllServers(UUID player) {
        return false;
    }

    @Override
    public boolean hasPermissionToUnbanPermanent(UUID player) {
        return false;
    }

    @Override
    public boolean hasPermissionToBanIP(UUID player) {
        return false;
    }

    @Override
    public boolean hasPermissionToUnbanIP(UUID player) {
        return false;
    }

    @Override
    public boolean hasExemptionFromBan(UUID player) {
        return false;
    }
}
