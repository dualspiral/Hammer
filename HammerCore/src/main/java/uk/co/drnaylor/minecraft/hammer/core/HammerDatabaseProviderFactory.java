package uk.co.drnaylor.minecraft.hammer.core;

import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.database.h2.H2FlatFileDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.database.mysql.MySqlDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.database.sqlite.SQLiteDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

final class HammerDatabaseProviderFactory {
    private HammerDatabaseProviderFactory() {}

    private final static Map<String, Factory> providerFactory = new HashMap<>();

    static {
        providerFactory.put("sqlite", (s, c) -> new SQLiteDatabaseProvider(String.format("%1$s%2$sdata%2$ssqlite.db", s.getDataFolder(), File.pathSeparator)));
        providerFactory.put("h2", (s, c) -> new H2FlatFileDatabaseProvider(String.format("%1$s%2$sdata%2$sh2.db", s.getDataFolder(), File.pathSeparator)));
        providerFactory.put("mysql", (s, c) -> {
            ConfigurationNode mysql = c.getConfig().getNode("mysql");
            return new MySqlDatabaseProvider(
                    mysql.getNode("host").getString(),
                    mysql.getNode("port").getInt(3306),
                    mysql.getNode("database").getString(),
                    mysql.getNode("username").getString(),
                    mysql.getNode("password").getString());
        });
    }

    static IDatabaseProvider createDatabaseProvider(WrappedServer server, HammerConfiguration config) throws HammerException {
        ConfigurationNode root = config.getConfig();
        String type = root.getNode("database-engine").getString("sqlite");
        Factory f = providerFactory.get(type.toLowerCase());
        if (f == null) {
            throw new HammerException("No database provider for type " + type);
        }

        try {
            return f.create(server, config);
        } catch (Exception e) {
            throw new HammerException("Unable to connect to the database.", e);
        }
    }

    @FunctionalInterface
    private interface Factory {
        IDatabaseProvider create(WrappedServer server, HammerConfiguration configuration) throws ClassNotFoundException, IOException;
    }
}
