package uk.co.drnaylor.minecraft.hammer.core.handlers;

import java.sql.SQLException;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

public class DatabaseConnection implements AutoCloseable {

    private final IDatabaseProvider provider;
    private IDatabaseGateway gateway;

    public DatabaseConnection(IDatabaseProvider provider) throws SQLException {
        this.provider = provider;
    }

    public void performStartupTasks() throws HammerException {
        openConnectionIfNotOpen();
        try {
            gateway.createTables();
        } catch (SQLException ex) {
            throw new HammerException("Unable to create the tables", ex);
        }
    }

    public BanHandler getBanHandler() throws HammerException {
        openConnectionIfNotOpen();
        return new BanHandler(gateway);
    }

    public PlayerHandler getPlayerHandler() throws HammerException {
        openConnectionIfNotOpen();
        return new PlayerHandler(gateway);
    }

    public ServerHandler getServerHandler() throws HammerException {
        openConnectionIfNotOpen();
        return new ServerHandler(gateway);
    }

    public String getNewExternalID() throws HammerException {
        openConnectionIfNotOpen();
        return new HammerExternalIDGenerator(this).generateExternalId();
    }

    public void startTransaction() throws HammerException {
        try {
            openConnectionIfNotOpen();
            gateway.startTransaction();
        } catch (SQLException ex) {
            throw new HammerException("Could not start transaction", ex);
        }
    }

    public void commitTransaction() throws HammerException {
        try {
            openConnectionIfNotOpen();
            gateway.commitTransaction();
        } catch (SQLException ex) {
            throw new HammerException("Could not commit transaction", ex);
        }
    }

    public void rollbackTransaction() throws HammerException {
        try {
            openConnectionIfNotOpen();
            gateway.rollbackTransaction();
        } catch (SQLException ex) {
            throw new HammerException("Could not rollback transaction", ex);
        }
    }

    @Override
    public void close() throws Exception {
        if (gateway != null) {
            gateway.close();
        }
    }

    private void openConnectionIfNotOpen() throws HammerException {
        try {
            if (gateway == null) {
                gateway = provider.openConnection();
            }
        } catch (SQLException ex) {
            throw new HammerException("Unable to connect to the Database.", ex);
        }
    }

}
