package uk.co.drnaylor.minecraft.hammer.core.data.input;

import java.util.Date;
import java.util.UUID;

public class HammerCreatePlayerBan {
    private final String externalID;
    private final UUID bannedBy;
    private final boolean isPerm;
    private final String reason;
    private final Integer serverId;
    private final UUID bannedUUID;
    private final Date tempBan;

    HammerCreatePlayerBan(String externalId, UUID bannedBy,
            boolean isPerm, String reason, Integer serverId,
            UUID bannedUUID, Date tempBan) {
        this.externalID = externalId;
        this.bannedBy = bannedBy;
        this.bannedUUID = bannedUUID;
        this.isPerm = isPerm;
        this.reason = reason;
        this.serverId = serverId;
        this.tempBan = tempBan;
    }

    public String getExternalID() {
        return externalID;
    }

    public UUID getBannedUUID() {
        return bannedUUID;
    }

    public UUID getStaffUUID() {
        return bannedBy;
    }

    public boolean isPermanent() {
        return isPerm;
    }
    
    public String getReason() {
        return reason;
    }

    public Integer getServerId() {
        return serverId;
    }

    public Date getTempBanExpiration() {
        return tempBan;
    }
}
