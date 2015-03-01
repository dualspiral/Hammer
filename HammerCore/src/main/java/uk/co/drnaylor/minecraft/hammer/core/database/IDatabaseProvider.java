package uk.co.drnaylor.minecraft.hammer.core.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface to allow for incremental data saving using various back ends..
 */
public interface IDatabaseProvider {

    /**
     * Opens a {@link Connection} to the database and provides an object to save
     * and load data from.
     *
     * @throws SQLException Thrown if the connection could not be opened.
     *
     * @return The {@link IDatabaseGateway}.
     */
    public IDatabaseGateway openConnection() throws SQLException;
}
