package uk.co.drnaylor.minecraft.hammer.core;

import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.database.mysql.MySqlDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

/**
 * A factory class that creates the correct {@link HammerCore} object.
 */
public class HammerCoreFactory {
    private HammerCoreFactory() { }

    /**
     * Creates a {@link HammerCore} object.
     *
     * @param server The {@link WrappedServer}
     * @return The {@link HammerCore} object
     * @throws ClassNotFoundException Thrown if the database driver cannot be found.
     */
    public static HammerCore createHammerCore(WrappedServer server, HammerConfiguration config) throws ClassNotFoundException {
        // Default to 3306
        ConfigurationNode root = config.getConfig();

        // TODO: Other back ends
        return withMysql(server, config);
    }

    private static HammerCore withMysql(WrappedServer server, HammerConfiguration config) throws ClassNotFoundException {
        ConfigurationNode root = config.getConfig();
        ConfigurationNode mysql = root.getNode("mysql");
        int portNo = mysql.getNode("port").getInt(3306);
        return new HammerCore(server, config,
                new MySqlDatabaseProvider(
                        mysql.getNode("host").getString(),
                        portNo,
                        mysql.getNode("database").getString(),
                        mysql.getNode("username").getString(),
                        mysql.getNode("password").getString()));
    }
}
