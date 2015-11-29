package uk.co.drnaylor.minecraft.hammer.core.database.sqlite;

import uk.co.drnaylor.minecraft.hammer.core.database.CommonDatabaseGateway;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

final class SQLiteDatabaseGateway extends CommonDatabaseGateway {

    SQLiteDatabaseGateway(Connection connection) {
        super(connection);
    }

    @Override
    public void createTables() throws SQLException {
        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS player_data (\n" +
                        "player_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
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
                        "    ban_id INTEGER primary key AUTOINCREMENT,\n" +
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
                        "    ip_ban_id integer primary key AUTOINCREMENT,\n" +
                        "    ip varchar(40) not null,\n" +
                        "    banned_by integer not null references player_data(player_id),\n" +
                        "    banned_until datetime,\n" +
                        "    from_server integer references server_data(server_id),\n" +
                        "    reason varchar(200) not null\n" +
                        ");");

        connection.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS audit (\n" +
                        "    audit_id integer primary key AUTOINCREMENT,\n" +
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
                "CREATE INDEX idx_action_1 ON audit(action)"
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
}
