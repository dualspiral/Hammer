package uk.co.drnaylor.minecraft.hammer.core;

import uk.co.drnaylor.minecraft.hammer.core.handlers.HammerExternalIDGenerator;
import java.sql.SQLException;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

public class HammerCore {

    private final IDatabaseProvider provider;
    private final HammerPluginActionProvider actionProvider;
    private final WrappedServer server;
    private HammerExternalIDGenerator externalIdGenerator = null;

    HammerCore(HammerPluginActionProvider actionProvider, WrappedServer server, IDatabaseProvider provider) {
        this.actionProvider = actionProvider;
        this.provider = provider;
        this.server = server;
    }

    /**
     * Returns the version of the core.
     * @return The version of the core.
     */
    public String getHammerCoreVersion() {
        return "0.2.1";
    }

    /**
     * Returns a provider that provides interfaces for interaction with server
     * specific APIs.
     *
     * @return The {@link HammerPluginActionProvider}
     */
    @Deprecated
    public HammerPluginActionProvider getActionProvider() {
        return actionProvider;
    }

    /**
     * Get the {@link WrappedServer} that represents the game server that is running.
     *
     * @return The {@link WrappedServer}
     */
    public WrappedServer getWrappedServer() {
        return server;
    }

    /**
     * Opens a database connection and gives access to the handlers.
     *
     * @return The {@link DatabaseConnection}
     * @throws HammerException Thrown if the connection fails to open.
     */
    public DatabaseConnection getDatabaseConnection() throws HammerException {
        try {
            return new DatabaseConnection(provider);
        } catch (SQLException ex) {
            throw new HammerException("There was an error opening the connection", ex);
        }
    }

    /**
     * Checks that the provided DB settings work and performs startup tasks.
     *
     * @param conn The connection to use.
     * @return <code>true</code> if so, <code>false</code> otherwise.
     */
    public boolean performStartupTasks(DatabaseConnection conn) {
        try {
            conn.performStartupTasks();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public String createTimeStringFromOffset(long timeOffset) {
        long time = timeOffset / 1000;
        long sec = time % 60;
        long min = (time / 60) % 60;
        long hour = (time / 3600) % 24;
        long day = time / 86400;

        StringBuilder sb = new StringBuilder();
        if (day > 0) {
            sb.append(day).append(" days");
        }

        if (hour > 0) {
            appendComma(sb);
            sb.append(hour).append(" hours");
        }

        if (min > 0) {
            appendComma(sb);
            sb.append(min).append(" minutes");
        }

        if (sec > 0) {
            appendComma(sb);
            sb.append(sec).append(" seconds");
        }

        if (sb.length() > 0) {
            return sb.toString();
        } else {
            return "unknown";
        }
    }

    private void appendComma(StringBuilder sb) {
        if (sb.length() > 0) {
            sb.append(", ");
        }
    }
}
