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
package uk.co.drnaylor.minecraft.hammer.sponge;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.AbstractConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ban.BanService;
import uk.co.drnaylor.minecraft.hammer.core.HammerConfiguration;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.*;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerConnectListenerCore;
import uk.co.drnaylor.minecraft.hammer.sponge.commands.HammerCommand;
import uk.co.drnaylor.minecraft.hammer.sponge.commands.SpongeAlias;
import uk.co.drnaylor.minecraft.hammer.sponge.commands.SpongeCommand;
import uk.co.drnaylor.minecraft.hammer.sponge.listeners.PlayerConnectListener;
import uk.co.drnaylor.minecraft.hammer.sponge.listeners.PlayerJoinListener;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextToTextColorCoverter;
import uk.co.drnaylor.minecraft.hammer.sponge.wrappers.SpongeWrappedServer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Sponge plugin entrypoint
 */
@Plugin(id = "hammer", name = "Hammer for Sponge", version = HammerSponge.VERSION, description = "Server Ban Management")
public class HammerSponge {

    public static final String VERSION = "0.6";

    @Inject private Game game;
    @Inject private Logger logger;
    @Inject @DefaultConfig(sharedRoot = false) private File defaultConfig;
    @Inject @DefaultConfig(sharedRoot = false) private ConfigurationLoader<CommentedConfigurationNode> configurationManager;

    private static HammerSponge instance;
    private HammerCore core;
    private boolean isLoaded = false;

    /**
     * Runs when the plugin is being initialised.
     *
     * @param event The event
     */
    @Listener
    public void onPluginInitialisation(GameInitializationEvent event) {
        try {
            logger.info("-----------------------------------------------------------------");
            logger.info("Welcome to Hammer for Sponge version " + VERSION);
            logger.info("Hammer will now perform some startup tasks. Stand by...");

            createCore();

            // TODO: Register the service.

            try (DatabaseConnection conn = this.core.getDatabaseConnection()) {
                // Special case. We want true/false, not an exception here.
                if (!core.performStartupTasks(conn)) {
                    return;
                }

                logger.info("Registering Hammer commands...");

                CommandSpec spec = CommandSpec.builder().executor(new HammerCommand(this))
                        .child(new SpongeCommand(new ReloadCommandCore(core)), "reload").build();
                game.getCommandManager().register(this, spec, "hammer");

                // Ban command
                game.getCommandManager().register(this, new SpongeCommand(new BanCommandCore(core)), "ban", "hban", "hammerban");
                game.getCommandManager().register(this, new SpongeCommand(new TempBanCommandCore(core)), "tempban", "tban", "htban", "hammertban");
                game.getCommandManager().register(this, new SpongeCommand(new UnbanCommandCore(core)), "unban", "hunban", "hammerunban");
                game.getCommandManager().register(this, new SpongeCommand(new CheckBanCommandCore(core)), "checkban", "hcheckban", "hammercheckban");

                ArrayList<String> arguments = new ArrayList<>();
                arguments.add("-a");
                game.getCommandManager().register(this, new SpongeAlias(game, "ban", arguments), "gban");

                ArrayList<String> arguments1 = new ArrayList<>();
                arguments.add("-p");
                game.getCommandManager().register(this, new SpongeAlias(game, "ban", arguments1), "permban", "hammerpban", "hpban", "pban");

                // Kick commands
                game.getCommandManager().register(this, new SpongeCommand(new KickCommandCore(core)), "kick", "hkick", "hammerkick");
                game.getCommandManager().register(this, new SpongeCommand(new KickAllCommandCore(core)), "kickall", "hkickall", "hammerkickall");

                // Import Player command
                game.getCommandManager().register(this, new SpongeCommand(new ImportPlayerCommand(core)), "importplayer", "himportplayer");

                // Upgrade to permban
                game.getCommandManager().register(this, new SpongeCommand(new UpgradeToPermBanCommandCore(core)), "toperm", "hammertoperm");
                game.getCommandManager().register(this, new SpongeCommand(new ReloadCommandCore(core)), "hammerreload");
                game.getCommandManager().register(this, new SpongeCommand(new UpdateBansCommandCore(core)), "updatebans", "hupdatebans");

                // IP Bans
                game.getCommandManager().register(this, new SpongeCommand(new BanIPCommandCore(core)), "ipban", "banip", "hipban", "hammeripban");
                game.getCommandManager().register(this, new SpongeCommand(new TempBanIPCommandCore(core)), "tempipban", "tempbanip", "htempipban", "hammertempipban");
                game.getCommandManager().register(this, new SpongeCommand(new UnbanIPCommandCore(core)), "ipunban", "unbanip", "hipunban", "hammeripunban");

                game.getCommandManager().register(this, new SpongeCommand(new ImportServerBansCommandCore(core)), "importserverbans");

                logger.info("Registering Hammer events...");

                // Register the events
                game.getEventManager().registerListeners(this, new PlayerConnectListener(logger, game, new PlayerConnectListenerCore(core)));
                game.getEventManager().registerListeners(this, new PlayerJoinListener(game, core.getPlayerJoinListenerCore()));

                core.postInit();
            }
        } catch (Exception ex) {
            logger.error("A fatal error has occurred. Hammer will now disable itself.");
            logger.error("Here. Have a stack trace to tell you why!");
            ex.printStackTrace();
            logger.info("-----------------------------------------------------------------");
            return;
        }

        isLoaded = true;
        instance = this;
    }

    /**
     * Runs when the server is starting.
     *
     * @param event The event
     */
    @Listener
    public void onServerStarting(GameStartingServerEvent event) {
        if (isLoaded) {
            // Once the server is starting, reset the text colour map.
            HammerTextToTextColorCoverter.init();
        }
    }

    /**
     * Runs when the server is about to stop.
     *
     * @param event The event
     */
    @Listener
    public void onServerStopping(GameStoppingServerEvent event) {
        if (isLoaded) {
            // Server is stopping, stop the runnable, but run it (sync) one last time.
            core.onStopping();
        }
    }

    /**
     * Returns the {@link HammerCore} that this plugin is running.
     *
     * @return The {@link HammerCore}.
     */
    public HammerCore getCore() {
        return core;
    }

    /**
     * Creates the {@link HammerCore} object.
     * @throws ClassNotFoundException The MySQL JDBC driver isn't on the classpath.
     * @throws IOException Configuration could not be loaded.
     */
    private void createCore() throws ClassNotFoundException, IOException, HammerException {
        // Temporary - configurate should automatically generate the config file, but a bug in the library that Sponge uses
        // means it doesn't. We create it here instead.
        if (!defaultConfig.exists()) {
            defaultConfig.createNewFile();
        }

        core = new HammerCore(
                new SpongeWrappedServer(this, game, logger),
                new HammerConfiguration((AbstractConfigurationLoader<? extends ConfigurationNode>) this.configurationManager));
    }

    public File getDefaultConfig() {
        return defaultConfig;
    }

    public static HammerSponge getInstance() {
        return instance;
    }

    /**
     * Gets the {@link BanService}
     *
     * @return The {@link BanService}
     */
    public static Optional<BanService> getBanService() {
        return Sponge.getGame().getServiceManager().provide(BanService.class);
    }
}
