package uk.co.drnaylor.minecraft.hammer.core;

import uk.co.drnaylor.minecraft.hammer.core.database.mysql.MySqlDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

/**
 * A factory class that creates the correct {@link HammerCore} object.
 */
public class HammerCoreFactory {
    private HammerCoreFactory() { }

    /**
     * Creates a {@link HammerCore} object that uses a MySQL database.
     *
     * @param server The {@link WrappedServer}
     * @param host The host name of the database.
     * @param port The port to connect to. 0 for default
     * @param databaseName The name of the database
     * @param user The user to connect as
     * @param password The password to use
     * @return The {@link HammerCore} object
     * @throws ClassNotFoundException Thrown if the MySQL driver cannot be found.
     */
    public static HammerCore CreateHammerCoreWithMySQL(WrappedServer server, String host, int port, String databaseName, String user, String password) throws ClassNotFoundException {
        // Default to 3306
        int portNo = port <= 0 ? 3306 : port;
        return new HammerCore(server, new MySqlDatabaseProvider(host, portNo, databaseName, user, password));
    }
}
