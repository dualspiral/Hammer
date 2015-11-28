package uk.co.drnaylor.minecraft.hammer.core.database.h2;

import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class H2FlatFileDatabaseProvider implements IDatabaseProvider {
    private final String connectionString;

    public H2FlatFileDatabaseProvider(String location) throws ClassNotFoundException, IOException {
        File f = new File(location);
        if (!f.exists()) {
            f.getParentFile().mkdirs();
            f.createNewFile();
        }

        this.connectionString = String.format("jdbc:h2:%s", location);

        Class.forName("org.h2.Driver");
    }

    @Override
    public IDatabaseGateway openConnection() throws SQLException {
        return new H2DatabaseGateway(DriverManager.getConnection(connectionString));
    }
}
