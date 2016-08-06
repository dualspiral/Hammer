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
import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Contains common DB routines that all implementations will use (thanks to standard SQL)
 */
public abstract class CommonDatabaseGateway implements IDatabaseGateway {

    protected final Connection connection;

    protected CommonDatabaseGateway(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void createTables() throws SQLException {
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS player_data (\n" +
                        "player_id INTEGER auto_increment PRIMARY KEY,\n" +
                        "uuid varchar(42) not null unique,\n" +
                        "    last_name varchar(20) not null,\n" +
                        "    last_seen datetime,\n" +
                        "    last_ip varchar(40)\n" +
                        ");");

        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS server_data (\n" +
                        "    server_id INTEGER PRIMARY KEY,\n" +
                        "    server_name varchar(30)\n" +
                        ");");

        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS player_bans (\n" +
                        "    ban_id INTEGER auto_increment primary key,\n" +
                        "    external_id varchar(20) unique not null,\n" +
                        "    banned datetime not null,\n" +
                        "    banned_player integer not null references player_data(player_id) on delete cascade,\n" +
                        "    banned_until datetime,\n" +
                        "    banned_by integer not null references player_data(player_id),\n" +
                        "    from_server integer references server_data(server_id),\n" +
                        "    is_permanent bit not null default 0,\n" +
                        "    reason varchar(200) not null\n" +
                        ");");

        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS ip_bans (\n" +
                        "    ip_ban_id integer auto_increment primary key,\n" +
                        "    ip varchar(40) not null,\n" +
                        "    banned datetime not null,\n" +
                        "    banned_by integer not null references player_data(player_id),\n" +
                        "    banned_until datetime,\n" +
                        "    from_server integer references server_data(server_id),\n" +
                        "    reason varchar(200) not null\n" +
                        ");");

        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS audit (\n" +
                        "    audit_id integer auto_increment primary key,\n" +
                        "    date datetime not null,\n" +
                        "    actor integer not null references player_data(player_id),\n" +
                        "    target integer references player_data(player_id),\n" +
                        "    server integer references server_data(server_id),\n" +
                        "    action varchar(50) not null,\n" +
                        "    event varchar(3000) not null" +
                        ");");

        String[] catchableStatements = new String[] {
                "CREATE UNIQUE INDEX idx_pl_ban_1 ON player_bans(banned_player, from_server);",
                "CREATE UNIQUE INDEX idx_ip_ban_1 ON ip_bans(ip, from_server);",
                "CREATE INDEX idx_actor_1 ON audit(actor)",
                "CREATE INDEX idx_target_1 ON audit(target)",
                "CREATE INDEX idx_datetime_1 ON audit(datetime)",
                "CREATE INDEX idx_action_1 ON audit(action)",
                "ALTER TABLE ip_bans ADD banned datetime NOT NULL"
        };

        for (String c : catchableStatements) {
            try {
                connection.createStatement().execute(c);
            }
            catch (SQLException e) {
                // Swallow.
            }
        }

        ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) as counter FROM player_data");
        rs.next();
        if (rs.getInt("counter") == 0) {
            // Console is UUID 00000000-0000-0000-0000-000000000000
            connection.createStatement().execute("INSERT INTO player_data(uuid, last_name, last_ip) VALUES ('00000000-0000-0000-0000-000000000000', '*Console*', '127.0.0.1');");
        }
    }

    /**
     * Gets a player's ban record for a server.
     * @param playerUUID The UUID of the player to inspect.
     * @param serverId The Id of the server to get information for.
     * @return A {@link HammerPlayerBan} object if the player has been banned, or <code>null</code>
     * @throws SQLException
     */
    @Override
    public HammerPlayerBan getPlayerBanForServer(UUID playerUUID, int serverId) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select b.external_id, b.banned, b.banned_until, b.from_server, s.server_name, b.is_permanent, b.reason, " +
                "p.uuid as banned_uuid, p.last_name as banned_name, " +
                "pb.uuid as banning_uuid, pb.last_name as banning_name " +
                "from player_bans b " +
                "inner join player_data p on b.banned_player = p.player_id " +
                "inner join player_data pb on b.banned_by = pb.player_id " +
                "left outer join server_data s on b.from_server = s.server_id " +
                "where p.uuid = ? AND (b.from_server is null or b.from_server = ?) order by b.from_server IS NULL DESC;");
        ps.setString(1, playerUUID.toString());
        ps.setInt(2, serverId);
        try
                (ResultSet set = ps.executeQuery()) {

            // We only need the first ban anyway
            if (!set.next()) {
                return null;
            }

            int serverValue = set.getInt("from_server");
            String serverName = serverValue == 0 ? "all servers" : set.getString("server_name");

            return new HammerPlayerBan(
                    set.getString("banned_name"),
                    UUID.fromString(set.getString("banned_uuid")),
                    set.getBoolean("is_permanent"),
                    UUID.fromString(set.getString("banning_uuid")),
                    set.getString("banning_name"),
                    set.getString("reason"),
                    set.getTimestamp("banned"),
                    set.getTimestamp("banned_until"),
                    serverValue == 0 ? null : serverValue,
                    serverName,
                    set.getString("external_id"));
        }
    }

    /**
     * Gets a player's ban record for any server.
     * @param playerUUID The UUID of the player to inspect.
     * @return A {@link List} of {@link HammerPlayerBan} objects
     * @throws SQLException
     */
    @Override
    public List<HammerPlayerBan> getPlayerBans(UUID playerUUID) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("select b.external_id, b.banned, b.banned_until, b.from_server, s.server_name, b.is_permanent, b.reason, " +
                "p.uuid as banned_uuid, p.last_name as banned_name, " +
                "pb.uuid as banning_uuid, pb.last_name as banning_name " +
                "from player_bans b " +
                "inner join player_data p on b.banned_player = p.player_id " +
                "inner join player_data pb on b.banned_by = pb.player_id " +
                "left outer join server_data s on b.from_server = s.server_id " +
                "where p.uuid = ? order by b.from_server IS NULL DESC;");
        ps.setString(1, playerUUID.toString());

        List<HammerPlayerBan> bans = new ArrayList<>();
        try
                (ResultSet set = ps.executeQuery()) {

            while (set.next()) {
                bans.add(createHammerPlayerBan(set));
            }
        }

        return bans;
    }

    @Override
    public void updatePlayer(UUID player, String lastName, String ip) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) as count FROM player_data WHERE uuid = ?");
        ps.setString(1, player.toString());
        int count;
        try (ResultSet set = ps.executeQuery()) {
            set.next();
            count = set.getInt("count");
        }

        if (count == 0) {
            // Insert.
            ps = connection.prepareStatement("insert into player_data(uuid, last_name, last_seen, last_ip) VALUES (?, ?, ? ,?);");
            ps.setString(1, player.toString());
            ps.setString(2, lastName);
            ps.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
            ps.setString(4, ip);
            ps.executeUpdate();
        } else {
            // Update.
            ps = connection.prepareStatement("update player_data set last_name = ?, last_seen = ?, last_ip = ? where uuid = ?;");
            ps.setString(4, player.toString());
            ps.setString(1, lastName);
            ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
            ps.setString(3, ip);
            ps.executeUpdate();
        }
    }

    @Override
    public void insertPlayerBan(HammerCreateBan.Player ban) throws SQLException, HammerException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO player_bans("
                + "external_id, banned_player, banned, banned_until, banned_by, from_server, is_permanent, reason) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?, ?);");
        ps.setString(1, ban.getExternalID());
        ps.setInt(2, getIdForPlayerFromUUID(ban.getBannedUUID()));
        ps.setTimestamp(3, getCurrentDate());
        if (ban.getTempBanExpiration() != null) {
            ps.setTimestamp(4, new java.sql.Timestamp(ban.getTempBanExpiration().getTime()));
        } else {
            ps.setNull(4, Types.DATE);
        }

        ps.setInt(5, getIdForPlayerFromUUID(ban.getStaffUUID()));
        if (ban.getServerId() == null) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, ban.getServerId());
        }

        ps.setBoolean(7, ban.isPermanent());
        ps.setString(8, ban.getReason());
        ps.executeUpdate();
    }

    @Override
    public void removePlayerBan(UUID player, Integer serverId) throws SQLException {
        PreparedStatement ps;
        if (serverId == null) {
            ps = connection.prepareStatement("DELETE FROM player_bans WHERE banned_player IN (SELECT player_id FROM player_data WHERE uuid = ?) AND from_server IS NULL");
            ps.setString(1, player.toString());
        } else {
            ps = connection.prepareStatement("DELETE FROM player_bans WHERE banned_player IN (SELECT player_id FROM player_data WHERE uuid = ?) AND from_server = ?");
            ps.setString(1, player.toString());
            ps.setInt(2, serverId);
        }

        ps.executeUpdate();
    }

    @Override
    public void removeAllPlayerBans(UUID player) throws SQLException {
        PreparedStatement ps;
        ps = connection.prepareStatement("DELETE FROM player_bans WHERE banned_player IN (SELECT player_id FROM player_data WHERE uuid = ?)");
        ps.setString(1, player.toString());

        ps.executeUpdate();
    }

    @Override
    public void removeExpiredPlayerBans(Integer serverId) throws SQLException {
        PreparedStatement ps;
        if (serverId == null) {
            ps = connection.prepareStatement("DELETE FROM player_bans WHERE banned_until IS NOT NULL AND banned_until < ?");
            ps.setTimestamp(1, getCurrentDate());
        } else if (serverId == 0) {
            ps = connection.prepareStatement("DELETE FROM player_bans WHERE banned_until IS NOT NULL AND banned_until < ? AND from_server IS NULL");
            ps.setTimestamp(1, getCurrentDate());
        } else {
            ps = connection.prepareStatement("DELETE FROM player_bans WHERE banned_until IS NOT NULL AND banned_until < ? AND from_server = ?");
            ps.setTimestamp(1, getCurrentDate());
            ps.setInt(2, serverId);
        }

        ps.executeUpdate();
    }

    @Override
    public List<HammerIPBan> getIPBans(InetAddress address) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "SELECT b.ip, b.banned_by, pb.uuid, pb.last_name, b.banned, b.banned_until, b.from_server, s.server_name, b.reason " +
                "FROM ip_bans b " +
                "inner join player_data pb on b.banned_by = pb.player_id " +
                "left outer join server_data s on b.from_server = s.server_id " +
                "WHERE ip = ?;"
        );

        ps.setString(1, address.getHostAddress());
        List<HammerIPBan> bans = new ArrayList<>();
        try (ResultSet set = ps.executeQuery()) {
            while (set.next()) {
                bans.add(createHammerIPBan(set));
            }
        }

        return bans;
    }

    @Override
    public void insertIPBan(HammerCreateBan.IP ban) throws SQLException, HammerException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO ip_bans("
            + "ip, banned_by, banned, banned_until, from_server, reason) "
            + "VALUES(?, ?, ?, ?, ?, ?);");

        ps.setString(1, ban.getBannedIP().getHostAddress());
        ps.setInt(2, getIdForPlayerFromUUID(ban.getStaffUUID()));
        ps.setTimestamp(3, getCurrentDate());
        if (ban.getTempBanExpiration() != null) {
            ps.setTimestamp(4, new java.sql.Timestamp(ban.getTempBanExpiration().getTime()));
        } else {
            ps.setNull(4, Types.DATE);
        }

        if (ban.getServerId() == null) {
            ps.setNull(5, Types.INTEGER);
        } else {
            ps.setInt(5, ban.getServerId());
        }

        ps.setString(6, ban.getReason());
        ps.executeUpdate();
    }

    @Override
    public void removeIPBan(InetAddress address, Integer serverId) throws SQLException {
        PreparedStatement ps;
        if (serverId == null) {
            ps = connection.prepareStatement(
                    "DELETE FROM ip_bans WHERE ip = ? AND server_id IS NULL;"
            );
        } else {
            ps = connection.prepareStatement(
                    "DELETE FROM ip_bans WHERE ip = ? AND server_id = ?;"
            );
            ps.setInt(2, serverId);
        }

        ps.setString(1, address.getHostAddress());
        ps.executeUpdate();
    }

    @Override
    public void removeAllIPBans(InetAddress address) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM ip_bans WHERE ip = ?;"
        );

        ps.setString(1, address.getHostAddress());
        ps.executeUpdate();
    }

    @Override
    public boolean externalIdExists(String id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(external_id) as counter FROM player_bans WHERE external_id = ?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        return rs.next() && rs.getInt("counter") > 0;

    }

    @Override
    @Deprecated
    public HammerPlayerInfo getPlayerInfo(UUID uuid) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT uuid, last_name, last_ip, last_seen FROM player_data WHERE uuid = ?");
        ps.setString(1, uuid.toString());

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new HammerPlayerInfo(uuid, rs.getString("last_name"), rs.getString("last_ip"), new Date(rs.getTimestamp("last_seen").getTime()));
            }

            return null;
        }
    }

    @Override
    @Deprecated
    public List<HammerPlayerInfo> getPlayerInfoFromName(String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT uuid, last_name, last_ip, last_seen FROM player_data WHERE LOWER(last_name) = ?");
        ps.setString(1, name.toLowerCase());

        List<HammerPlayerInfo> player = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                player.add(new HammerPlayerInfo(UUID.fromString(rs.getString("uuid")), rs.getString("last_name"), rs.getString("last_ip"), new Date(rs.getTimestamp("last_seen").getTime())));
            }
        }

        return player;
    }

    @Override
    public List<HammerPlayerBan> getServerBans(Set<UUID> players, int server) throws SQLException {
        // Create the string with the UUIDs in.
        StringBuilder sb = new StringBuilder();
        for (UUID player : players) {
            if (sb.length() > 0) {
                sb.append(",");
            }

            sb.append("'").append(player.toString()).append("'");
        }

        List<HammerPlayerBan> banList = new ArrayList<>();

        // I hate dynamic SQL, but with UUIDs, I guess it's fine...
        try (PreparedStatement ps = connection.prepareStatement("SELECT b.external_id, b.banned, b.banned_until, b.from_server, s.server_name, b.is_permanent, b.reason," +
                "p.uuid as banned_uuid, p.last_name as banned_name," +
                "pb.uuid as banning_uuid, pb.last_name as banning_name " +
                "from player_bans b " +
                "inner join player_data p on b.banned_player = p.player_id " +
                "inner join player_data pb on b.banned_by = pb.player_id " +
                "left outer join server_data s on b.from_server = s.server_id " +
                "where p.uuid IN (" + sb.toString() + ") and (b.from_server = ? OR b.from_server IS NULL);")) {
            ps.setInt(1, server);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    banList.add(createHammerPlayerBan(rs));
                }
            }
        }

        return banList;
    }

    @Override
    @Deprecated
    public HammerPlayerInfo getLastPlayerInfoFromName(String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT uuid, last_name, last_ip, last_seen FROM player_data WHERE LOWER(last_name) = ? ORDER BY last_seen DESC LIMIT 1");
        ps.setString(1, name.toLowerCase());

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new HammerPlayerInfo(UUID.fromString(rs.getString("uuid")), rs.getString("last_name"), rs.getString("last_ip"), new Date(rs.getTimestamp("last_seen").getTime()));
            }
        }

        return null;
    }

    @Override
    public void updateServerName(int serverId, String serverName) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(*) as count FROM server_data WHERE server_id = ?");
        ps.setInt(1, serverId);
        int count;
        try (ResultSet set = ps.executeQuery()) {
            set.next();
            count = set.getInt("count");
        }

        if (count == 0) {
            // Insert.
            ps = connection.prepareStatement("insert into server_data(server_id, server_name) VALUES (?, ?);");
            ps.setInt(1, serverId);
            ps.setString(2, serverName);
        } else {
            // Update.
            ps = connection.prepareStatement("update server_data set server_name = ? where server_id = ?;");
            ps.setString(1, serverName);
            ps.setInt(2, serverId);
        }

        ps.executeUpdate();
    }

    @Override
    public void updateBanToPermanent(HammerPlayerBan ban) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE player_bans SET is_permanent = 1 WHERE external_id = ?;"
        )) {
            ps.setString(1, ban.getExternalId());
            ps.execute();
        }
    }

    @Override
    public void insertAuditEntry(AuditEntry entry) throws SQLException, HammerException {
        PreparedStatement ps = connection.prepareStatement("INSERT INTO audit(date, actor, target, server, action, event) " +
                "VALUES (?, ?, ?, ?, ?, ?)");

        ps.setTimestamp(1, new Timestamp(entry.getDate().getTime()));
        ps.setInt(2, getIdForPlayerFromUUID(entry.getActor()));
        if (entry.getTarget() == null) {
            ps.setNull(3, Types.INTEGER);
        } else {
            ps.setInt(3, getIdForPlayerFromUUID(entry.getTarget()));
        }

        ps.setInt(4, entry.getServerId());
        ps.setString(5, entry.getActionType().name());
        ps.setString(6, entry.getEvent());
        ps.execute();
    }

    /**
     * Closes the connection. Implements java.lang.AutoClosable.
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        if (!connection.isClosed()) {
            if (!connection.getAutoCommit()) {
                this.rollbackTransaction();
            }

            connection.close();
        }
    }

    private java.sql.Timestamp getCurrentDate() {
        return new java.sql.Timestamp(new Date().getTime());
    }

    private Integer getIdForPlayerFromUUID(UUID uuid) throws SQLException, HammerException {
        PreparedStatement ps = connection.prepareStatement("SELECT player_id FROM player_data WHERE uuid = ?");
        ps.setString(1, uuid.toString());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("player_id");
        }

        throw new HammerException("No player with that UUID exists.");
    }

    private HammerPlayerBan createHammerPlayerBan(ResultSet set) throws SQLException {
        int serverValue = set.getInt("from_server");
        String serverName = serverValue == 0 ? "all servers" : set.getString("server_name");
        return new HammerPlayerBan(
                set.getString("banned_name"),
                UUID.fromString(set.getString("banned_uuid")),
                set.getBoolean("is_permanent"),
                UUID.fromString(set.getString("banning_uuid")),
                set.getString("banning_name"),
                set.getString("reason"),
                set.getTimestamp("banned"),
                set.getTimestamp("banned_until"),
                serverValue == 0 ? null : serverValue,
                serverName,
                set.getString("external_id"));
    }

    private HammerIPBan createHammerIPBan(ResultSet set) throws SQLException {
        int serverValue = set.getInt("from_server");
        String serverName = serverValue == 0 ? "all servers" : set.getString("server_name");
        return new HammerIPBan(
                UUID.fromString(set.getString("banned_by")),
                set.getString("last_name"),
                set.getString("reason"),
                set.getTimestamp("banned"),
                set.getTimestamp("banned_until"),
                serverValue == 0 ? null : serverValue,
                serverName
        );
    }

    @Override
    public void startTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }

    @Override
    public void commitTransaction() throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
    }

    @Override
    public void rollbackTransaction() throws SQLException {
        connection.rollback();
        connection.setAutoCommit(true);
    }
}
