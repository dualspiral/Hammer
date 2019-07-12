/*
 * This file is part of Hammer, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Daniel Naylor
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.co.drnaylor.minecraft.hammer.core.handlers;

import uk.co.drnaylor.minecraft.hammer.core.data.HammerIPBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreateBan;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BanHandler {

    private final IDatabaseGateway dg;

    BanHandler(IDatabaseGateway dg) {
        this.dg = dg;
    }

    public void createPlayerBan(HammerCreateBan.Player ban) throws HammerException {
        try {
            dg.insertPlayerBan(ban);
        } catch (Exception ex) {
            throw new HammerException("An error occurred setting the player ban.", ex);
        }
    }

    public void createIPBan(HammerCreateBan.IP ban) throws HammerException {
        try {
            dg.insertIPBan(ban);
        } catch (Exception ex) {
            throw new HammerException("An error occurred setting the IP ban.", ex);
        }
    }

    public List<HammerIPBan> getIPBans(InetAddress addr) throws HammerException {
        try {
            return dg.getIPBans(addr);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the IP bans.", ex);
        }
    }

    public List<HammerIPBan> getIPBansForServer(int serverId) throws HammerException {
        try {
            return dg.getIPBansForServer(serverId)
                    .stream().filter(b -> b.getServerId() == null || b.getServerId() == serverId)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new HammerException("An error occurred getting the IP bans.", e);
        }
    }

    public List<HammerIPBan> getIPBanForServer(InetAddress addr, int serverId) throws HammerException {
        return getIPBans(addr).stream().filter(b -> b.getServerId() == null || b.getServerId() == serverId)
                .collect(Collectors.toList());
    }

    public boolean isIPBannedFromServer(InetAddress addr, int serverId) throws HammerException {
        return !getIPBanForServer(addr, serverId).isEmpty();
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

    public List<HammerPlayerBan> getPlayerBansForServer(int serverId) throws HammerException {
        try {
            dg.removeExpiredPlayerBans(null);
            return dg.getPlayerBansForServer(serverId);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the player bans.", ex);
        }
    }

    public List<HammerPlayerBan> getPlayerBansForServer(Set<UUID> players, int serverId) throws HammerException {
        if (players.isEmpty()) {
            return new ArrayList<>();
        }

        try {
            dg.removeExpiredPlayerBans(null);
            return dg.getServerBans(players, serverId);
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

    public void unbanIpFromServer(InetAddress ip, int serverId) throws HammerException {
        try {
            dg.removeIPBan(ip, serverId);
        } catch (Exception ex) {
            throw new HammerException("An error occurred removing the IP ban.", ex);
        }
    }

    public void unbanIpFromAllServers(InetAddress ip) throws HammerException {
        try {
            dg.removeAllIPBans(ip);
        } catch (Exception ex) {
            throw new HammerException("An error occurred removing the IP bans.", ex);
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

    public boolean isExternalIdUsed(String externalId) throws HammerException {
        try {
            return dg.externalIdExists(externalId);
        } catch (Exception ex) {
            throw new HammerException("There was an issue checking the External ID.", ex);
        }
    }
}
