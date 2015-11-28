package uk.co.drnaylor.minecraft.hammer.core.database.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;

/**
 * An adapter to interact with a MySQL database.
 */
public final class MySqlDatabaseProvider implements IDatabaseProvider {

    private final String username;
    private final String password;
    private final String connectionPath;

    /**
     * Constructs this {@link IDatabaseProvider}
     * 
     * @param host The host to connect to
     * @param port The port number to use
     * @param database The name of the database
     * @param username The username to  connect with
     * @param password The password to use when connecting.
     * @throws ClassNotFoundException 
     */
    public MySqlDatabaseProvider(String host, int port, String database, String username, String password) throws ClassNotFoundException {
        this.username = username;
        this.password = password;
        this.connectionPath = String.format("jdbc:mysql://%s:%s/%s", host, port, database);

        Class.forName("com.mysql.jdbc.Driver");
    }

    @Override
    public IDatabaseGateway openConnection() throws SQLException {
        return new MySqlDatabaseGateway(DriverManager.getConnection(connectionPath, username, password));
    }
}
