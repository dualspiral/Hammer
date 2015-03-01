package uk.co.drnaylor.minecraft.hammer.core.handlers;

import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

public class ServerHandler {
    private final IDatabaseGateway dg;

    public ServerHandler(IDatabaseGateway dg) {
        this.dg = dg;
    }

    public void updateServerNameForId(int serverId, String serverName) throws Exception {
        try {
            dg.updateServerName(serverId, serverName);
        } catch (Exception ex) {
            throw new HammerException("An error occurred updating the server.", ex);
        }
    }
}
