package uk.co.drnaylor.minecraft.hammer.core.handlers;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerIPBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

public class BanHandler {

    private final IDatabaseGateway dg;

    BanHandler(IDatabaseGateway dg) {
        this.dg = dg;
    }

    public void createServerBan(HammerCreatePlayerBan ban) throws HammerException {
        try {
            dg.insertPlayerBan(ban);
        } catch (Exception ex) {
            throw new HammerException("An error occurred setting the player ban.", ex);
        }
    }

    public boolean isBannedFromServer(UUID player, int serverId) throws HammerException {
        return getPlayerBanForServer(player, serverId) != null;
    }

    public List<HammerPlayerBan> getPlayerBans(UUID player) throws HammerException {
        try {
            dg.removeExpiredPlayerBans(null);
            return dg.getPlayerBans(player);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the player ban.", ex);
        }
    }

    public List<HammerPlayerBan> getPlayerBansForServer(Set<UUID> players) throws HammerException {
        try {
            dg.removeExpiredPlayerBans(null);
            return dg.getServerBans(players);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the player bans.", ex);
        }
    }

    public HammerPlayerBan getPlayerBanForServer(UUID player, int serverId) throws HammerException {
        try {
            dg.removeExpiredPlayerBans(serverId);
            return dg.getPlayerBanForServer(player, serverId);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the player ban.", ex);
        }
    }

   public HammerIPBan getIpBan(String ip) {
        return null;
    }

    public void unbanFromServer(UUID playerToBan, int serverId) throws HammerException {
        try {
            dg.removePlayerBan(playerToBan, serverId);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the player ban.", ex);
        }
    }

    public void unbanFromAllServers(UUID playerToBan) throws HammerException {
        try {
            dg.removeAllPlayerBans(playerToBan);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the player ban.", ex);
        }
    }

    public boolean upgadeToPerm(UUID playerToBan, int serverId) throws HammerException {
        try {
            HammerPlayerBan hpb = dg.getPlayerBanForServer(playerToBan, serverId);
            if (hpb.isPermBan()) {
                dg.updateBanToPermanent(hpb);
                return true;
            }

            return false;
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting and/or setting the player ban.", ex);
        }
    }

    public boolean isIpBanned(String ip) {
        return getIpBan(ip) != null;
    }

    public boolean createIpBan(HammerIPBan ban) {
        return false;
    }

    public boolean unbanIp(String ip) {
        return false;
    }

    public boolean isExternalIdUsed(String externalId) throws HammerException {
        try {
            return dg.externalIdExists(externalId);
        } catch (Exception ex) {
            throw new HammerException("There was an issue checking the External ID.", ex);
        }
    }
}
