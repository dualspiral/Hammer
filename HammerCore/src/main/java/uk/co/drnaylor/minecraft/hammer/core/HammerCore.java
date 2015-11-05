package uk.co.drnaylor.minecraft.hammer.core;

import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerJoinListenerCore;
import uk.co.drnaylor.minecraft.hammer.core.runnables.BanCheckRunnable;
import uk.co.drnaylor.minecraft.hammer.core.runnables.HammerPlayerUpdateRunnable;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

import java.util.logging.Level;

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

        this.server.getLogger().info("Loading Hammer Core version " + getHammerCoreVersion());
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
            server.getLogger().info("Establishing DB link and creating any missing tables...");
            ConfigurationNode cn = this.config.getConfig().getNode("server");
            conn.performStartupTasks();
            conn.getServerHandler().updateServerNameForId(cn.getNode("id").getInt(), cn.getNode("name").getString("Unknown"));

            server.getLogger().info("Connection to DB was successful and all required tables were created.");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        server.getLogger().error("Your DB credentials were rejected, or do not allow the required access to the database. Hammer will now disable itself.");
        server.getLogger().error("-----------------------------------------------------------------");
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

        server.getLogger().info("Hammer has successfully initialised and is managing your bans.");
        server.getLogger().info("-----------------------------------------------------------------");
    }

    public void onStopping() {
        playerJoinListenerCore.getRunnable().run();
    }
}
