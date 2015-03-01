package uk.co.drnaylor.minecraft.hammer.core.data;

import java.util.Date;
import java.util.UUID;

public class HammerPlayerBan extends HammerBan {

    private final String bannedName;
    private final UUID bannedUUID;
    private final boolean permanent;
    private final String externalId;

    public HammerPlayerBan(String bannedName, UUID bannedUUID, boolean isPermanent, 
            UUID banningStaffUUID, String banningStaffName, String reason, 
            Date dateOfBan, Date dateOfUnban, Integer serverId, String serverName, String externalId) {
        super(banningStaffUUID, banningStaffName, reason, dateOfBan, dateOfUnban, serverId, serverName);
        this.bannedName = bannedName;
        this.bannedUUID = bannedUUID;
        this.permanent = isPermanent;
        this.externalId = externalId;
    }

    public String getBannedLastKnownName() {
        return bannedName;
    }

    public UUID getBannedUUID() {
        return bannedUUID;
    }

    public boolean isPermBan() {
        return permanent;
    }

    public String getExternalId() {
        return externalId;
    }
}
