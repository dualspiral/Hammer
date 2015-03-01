package uk.co.drnaylor.minecraft.hammer.core.data;

import java.util.Date;
import java.util.UUID;

public class HammerIPBan extends HammerBan {

    public HammerIPBan(UUID banningStaffUUID, String banningStaffName, String reason, Date dateOfBan, Date dateOfUnban, Integer serverId, String serverName) {
        super(banningStaffUUID, banningStaffName, reason, dateOfBan, dateOfUnban, serverId, serverName);
    }
    
}
