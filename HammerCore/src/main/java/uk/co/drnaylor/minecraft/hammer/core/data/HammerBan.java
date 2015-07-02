package uk.co.drnaylor.minecraft.hammer.core.data;

import java.util.Date;
import java.util.UUID;

public abstract class HammerBan {

    private final UUID banningStaffUUID;
    private final String banningStaffName;
    private final String reason;
    private final Date dateOfBan;
    private final Date dateOfUnban;
    
    /* Server Id can be null */
    private final Integer serverId;
    private final String serverName;

    HammerBan(UUID banningStaffUUID, String banningStaffName, String reason, Date dateOfBan, Date dateOfUnban, Integer serverId, String serverName) {
        this.banningStaffUUID = banningStaffUUID;
        this.banningStaffName = banningStaffName;
        this.reason = reason;
        this.dateOfBan = dateOfBan;
        this.dateOfUnban = dateOfUnban;
        this.serverId = serverId;
        this.serverName = serverName;
    }

    public String getReason() {
        return reason;
    }

    public UUID getBanningStaff() {
        return banningStaffUUID;
    }

    public String getBanningStaffName() {
        return banningStaffName;
    }

    public Date getDateOfBan() {
        return dateOfBan;
    }

    public Date getDateOfUnban() {
        return dateOfUnban;
    }

    public boolean isTempBan() {
        return this.dateOfUnban != null;
    }

    public Integer getServerId() {
        return serverId;
    }

    public String getServerName() { return serverName; }
}
