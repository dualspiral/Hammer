package uk.co.drnaylor.minecraft.hammer.core;

import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerJoinListenerCore;
import uk.co.drnaylor.minecraft.hammer.core.runnables.BanCheckRunnable;
import uk.co.drnaylor.minecraft.hammer.core.runnables.HammerPlayerUpdateRunnable;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

public class HammerCore {

    private final IDatabaseProvider provider;
    private final WrappedServer server;
    private final HammerConfiguration config;
    private final PlayerJoinListenerCore playerJoinListenerCore;

    HammerCore(WrappedServer server, HammerConfiguration config, IDatabaseProvider provider) {
        this.provider = provider;
        this.config = config;
        this.server = server;
        this.playerJoinListenerCore = new PlayerJoinListenerCore(new HammerPlayerUpdateRunnable(this));
    }

    /**
     * Returns the version of the core.
     * @return The version of the core.
     */
    public String getHammerCoreVersion() {
        return "0.3.3";
    }

    /**
     * Gets the {@link HammerConfiguration} that contains the config file.
     *
     * @return The config file.
     */
    public HammerConfiguration getConfig() {
        return config;
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
        return new DatabaseConnection(provider);
    }

    /**
     * Checks that the provided DB settings work and performs startup tasks.
     *
     * @param conn The connection to use.
     * @return <code>true</code> if so, <code>false</code> otherwise.
     */
    public boolean performStartupTasks(DatabaseConnection conn) {
        try {
            ConfigurationNode cn = this.config.getConfig().getNode("server");
            conn.performStartupTasks();
            conn.getServerHandler().updateServerNameForId(cn.getNode("id").getInt(), cn.getNode("name").getString("Unknown"));
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public PlayerJoinListenerCore getPlayerJoinListenerCore() {
        return playerJoinListenerCore;
    }

    public void postInit() {
        // Start the scheduled task...
        server.getLogger().info("Starting the async tasks...");

        // Join listener
        getWrappedServer().getScheduler().createAsyncRecurringTask(playerJoinListenerCore.getRunnable(), 20);

        // Ban checking
        getWrappedServer().getScheduler().createAsyncRecurringTask(new BanCheckRunnable(this), 60);
    }

    public void onStopping() {
        playerJoinListenerCore.getRunnable().run();
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
