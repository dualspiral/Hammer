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
package uk.co.drnaylor.minecraft.hammer.core.database;

import uk.co.drnaylor.minecraft.hammer.core.audit.AuditEntry;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerIPBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreateBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IDatabaseGateway extends AutoCloseable {

    void createTables() throws SQLException;

    /**
     * Gets a player's ban record for a server.
     * @param playerUUID The UUID of the player to inspect.
     * @param serverId The Id of the server to get information for.
     * @return A {@link HammerPlayerBan} object if the player has been banned, or <code>null</code>
     * @throws SQLException
     */
    HammerPlayerBan getPlayerBanForServer(UUID playerUUID, int serverId) throws SQLException;

    /**
     * Gets a player's ban record for any server.
     * @param playerUUID The UUID of the player to inspect.
     * @return A {@link List} of {@link HammerPlayerBan} objects
     * @throws SQLException
     */
    List<HammerPlayerBan> getPlayerBans(UUID playerUUID) throws SQLException;

    void updatePlayer(UUID player, String lastName, String ip) throws SQLException;

    void insertPlayerBan(HammerCreateBan.Player ban) throws SQLException, HammerException;

    void removePlayerBan(UUID player, Integer serverId) throws SQLException;

    void removeAllPlayerBans(UUID player) throws SQLException;

    void removeExpiredPlayerBans(Integer serverId) throws SQLException;

    List<HammerIPBan> getIPBans(InetAddress address) throws SQLException;

    void insertIPBan(HammerCreateBan.IP ban) throws SQLException, HammerException;

    void removeIPBan(InetAddress address, Integer serverId) throws SQLException;

    void removeAllIPBans(InetAddress address) throws SQLException;

    /**
     * Gets a value indicating whether the specified external ID exists.
     * @param externalId The External ID to check
     * @return <code>true</code> if it already exists, <code>false</code> otherwise.
     * @throws SQLException
     */
    boolean externalIdExists(String externalId) throws SQLException;

    HammerPlayerInfo getPlayerInfo(UUID uuid) throws SQLException;

    /**
     * Gets a {@link HammerPlayerInfo} object from the provided name. It gets the last known UUID.
     * Due to name changes, there is no guarantee that the names will be unique.
     * The search is case-insenstive.
     *
     * @param name The name to search for.
     * @return The player. May be null.
     * @throws SQLException Thrown if the DB finds an error.
     */
    HammerPlayerInfo getLastPlayerInfoFromName(String name) throws SQLException;

    /**
     * Gets a list of {@link HammerPlayerInfo} objects from the provided name.
     * Due to name changes, there is no guarantee that the names will be unique.
     * The search is case-insenstive.
     * 
     * @param name The name to search for.
     * @return The list of players. May be empty.
     * @throws SQLException Thrown if the DB finds an error.
     */
    List<HammerPlayerInfo> getPlayerInfoFromName(String name) throws SQLException;

    List<HammerPlayerBan> getServerBans(Set<UUID> players, int server) throws SQLException;

    void updateServerName(int serverId, String serverName) throws SQLException;

    void updateBanToPermanent(HammerPlayerBan ban) throws SQLException;

    void startTransaction() throws SQLException;

    void commitTransaction() throws SQLException;

    void rollbackTransaction() throws SQLException;

    void insertAuditEntry(AuditEntry entry) throws SQLException, HammerException;
}
