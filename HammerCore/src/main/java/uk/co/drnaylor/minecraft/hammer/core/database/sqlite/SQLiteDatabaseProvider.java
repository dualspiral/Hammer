package uk.co.drnaylor.minecraft.hammer.core.database.sqlite;

import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * An adapter to interact with a SQLite database.
 */
public final class SQLiteDatabaseProvider implements IDatabaseProvider {

    private final String connectionString;

    public SQLiteDatabaseProvider(String location) throws ClassNotFoundException, IOException {
        File f = new File(location);
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            f.createNewFile();
        }

        this.connectionString = String.format("jdbc:sqlite:%s", location);

        Class.forName("org.sqlite.JDBC");
    }

    @Override
    public IDatabaseGateway openConnection() throws SQLException {
        return new SQLiteDatabaseGateway(DriverManager.getConnection(connectionString));
    }
}
