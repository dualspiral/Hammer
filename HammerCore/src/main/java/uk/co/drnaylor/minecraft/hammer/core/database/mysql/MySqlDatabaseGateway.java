package uk.co.drnaylor.minecraft.hammer.core.database.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayer;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;

class MySqlDatabaseGateway implements IDatabaseGateway {

    private final Connection connection;

    MySqlDatabaseGateway(Connection connection) {
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
            "    banned_by integer not null references player_data(player_id),\n" +
            "    banned_until datetime,\n" +
            "    from_server integer references server_data(server_id),\n" +
            "    reason varchar(200) not null\n" +
            ");");

        try {
            connection.createStatement().execute(
                "CREATE UNIQUE INDEX idx_pl_ban_1 ON player_bans(banned_player, from_server);");

            connection.createStatement().execute(
                "CREATE UNIQUE INDEX idx_ip_ban_1 ON ip_bans(ip, from_server);");
        }
        catch (SQLException e) {
            // Swallow.
        }

        
        ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) as counter FROM player_data");
        rs.next();
        if (rs.getInt("counter") == 0) {
            connection.createStatement().execute("ALTER TABLE player_data AUTO_INCREMENT 0;");
            
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
            HammerPlayerBan pb = new HammerPlayerBan(
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

            return pb;
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
                int serverValue = set.getInt("from_server");
                String serverName = serverValue == 0 ? "all servers" : set.getString("server_name");
                HammerPlayerBan pb = new HammerPlayerBan(
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

                bans.add(pb);
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
    public void insertPlayerBan(HammerCreatePlayerBan ban) throws SQLException {
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
    public boolean externalIdExists(String id) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT COUNT(external_id) as counter FROM player_bans WHERE external_id = ?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("counter") > 0;
        }

        return false;
    }

    @Override
    public HammerPlayer getPlayer(UUID uuid) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT uuid, last_name, last_ip FROM player_data WHERE uuid = ?");
        ps.setString(1, uuid.toString());

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new HammerPlayer(uuid, rs.getString("last_name"), rs.getString("last_ip"));
            }

            return null;
        }
    }

    @Override
    public List<HammerPlayer> getPlayerFromName(String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT uuid, last_name, last_ip FROM player_data WHERE LOWER(last_name) = ?");
        ps.setString(1, name.toLowerCase());

        List<HammerPlayer> player = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                player.add(new HammerPlayer(UUID.fromString(rs.getString("uuid")), rs.getString("last_name"), rs.getString("last_ip")));
            }
        }

        return player;
    }

    @Override
    public HammerPlayer getLastPlayerFromName(String name) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT uuid, last_name, last_ip FROM player_data WHERE LOWER(last_name) = ? ORDER BY last_seen DESC LIMIT 1");
        ps.setString(1, name.toLowerCase());

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                return new HammerPlayer(UUID.fromString(rs.getString("uuid")), rs.getString("last_name"), rs.getString("last_ip"));
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

    private Integer getIdForPlayerFromUUID(UUID uuid) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT player_id FROM player_data WHERE uuid = ?");
        ps.setString(1, uuid.toString());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("player_id");
        }

        return null;
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
