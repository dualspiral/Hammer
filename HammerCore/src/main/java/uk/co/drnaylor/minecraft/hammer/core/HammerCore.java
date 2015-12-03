/*
 * This file is part of Hammer, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Daniel Naylor
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.co.drnaylor.minecraft.hammer.core;

import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.audit.AuditHelper;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerJoinListenerCore;
import uk.co.drnaylor.minecraft.hammer.core.runnables.BanCheckRunnable;
import uk.co.drnaylor.minecraft.hammer.core.runnables.HammerPlayerUpdateRunnable;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedSchedulerTask;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

import java.io.IOException;

public class HammerCore {

    private IDatabaseProvider provider;
    private final WrappedServer server;
    private final HammerConfiguration config;
    private final PlayerJoinListenerCore playerJoinListenerCore;
    private final AuditHelper auditHelper;
    private WrappedSchedulerTask banTask = null;
    private WrappedSchedulerTask playerJoinListener = null;

    public HammerCore(WrappedServer server, HammerConfiguration config) throws HammerException {
        this.config = config;
        this.server = server;
        this.playerJoinListenerCore = new PlayerJoinListenerCore(new HammerPlayerUpdateRunnable(this));

        this.provider = HammerDatabaseProviderFactory.createDatabaseProvider(server, config);

        this.server.getLogger().info("Loading Hammer Core version " + getHammerCoreVersion());
        this.auditHelper = new AuditHelper(this);
    }

    /**
     * Returns the version of the core.
     * @return The version of the core.
     */
    public String getHammerCoreVersion() {
        return "0.5.1";
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
     * Gets the {@link AuditHelper} for auditing purposes.
     *
     * @return The {@link AuditHelper}
     */
    public AuditHelper getAuditHelper() {
        return auditHelper;
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
        playerJoinListener = getWrappedServer().getScheduler().createAsyncRecurringTask(playerJoinListenerCore.getRunnable(), 20);
        setupBanTask();

        server.getLogger().info("Hammer has successfully initialised and is managing your bans.");
        server.getLogger().info("-----------------------------------------------------------------");
    }

    /**
     * Synchronised for ensuring that the ban task is not set off twice.
     */
    public synchronized void setupBanTask() {
        if (banTask != null) {
            banTask.cancelTask();
        }

        if (getConfig().getConfig().getNode("pollBans", "enable").getBoolean()) {
            // Ban checking
            banTask = getWrappedServer().getScheduler().createAsyncRecurringTask(new BanCheckRunnable(this), getConfig().getConfig().getNode("pollBans", "period").getInt());
        } else {
            banTask = null;
        }
    }

    public void onStopping() {
        playerJoinListener.cancelTask();
        if (banTask != null) {
            banTask.cancelTask();
        }

        playerJoinListenerCore.getRunnable().run();
    }

    @Deprecated
    public void reloadConfig() throws IOException, HammerException {
        reloadConfig(false);
    }

    /**
     * Reloads the configuration.
     *
     * @param reloadDatabase <code>true</code> if the database should be reloaded.
     * @throws IOException Thrown if the config file could not be read.
     * @throws HammerException Thrown if the database could not be switched.
     */
    public void reloadConfig(boolean reloadDatabase) throws IOException, HammerException {
        config.reloadConfig();
        setupBanTask();
        if (reloadDatabase) {
            // Will throw if there is an issue.
            this.provider = HammerDatabaseProviderFactory.createDatabaseProvider(server, config);
        }
    }
}
