package uk.co.drnaylor.minecraft.hammer.sponge;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ServiceReference;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.util.event.Subscribe;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerCoreFactory;
import uk.co.drnaylor.minecraft.hammer.core.HammerPluginActionProvider;
import uk.co.drnaylor.minecraft.hammer.sponge.coreimpl.*;
import uk.co.drnaylor.minecraft.hammer.sponge.services.HammerBanService;

import java.io.File;
import java.io.IOException;

/**
 * Sponge plugin entrypoint
 */
@Plugin(id = "hammersponge", name = "Hammer for Sponge", version = "0.2")
public class HammerSponge {

    @Inject
    private Game game;

    @Inject
    private Logger logger;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private File defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configurationManager;

    private HammerCore core;

    /**
     * Runs when the plugin is being initialised.
     *
     * @param event The event
     */
    @Subscribe
    public void onPluginInitialisation(InitializationEvent event) {
        try {
            createCore();

            // Register the service.
        } catch (Exception ex) {
            // Do some stuff
        }
    }

    /**
     * Runs when the server has started.
     *
     * @param event The event
     */
    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        // Create the core.

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
    protected final void createCore() throws ClassNotFoundException, IOException {
        // TODO: Check this. It's probably wrong right now.
        CommentedConfigurationNode configNode = configurationManager.load();
        CommentedConfigurationNode mySqlNode = configNode.getNode("mysql");

        configurationManager.save(configNode);

        core = HammerCoreFactory.CreateHammerCoreWithMySQL(
                createActionProvider(),
                mySqlNode.getNode("host").getString(),
                mySqlNode.getNode("port").getInt(),
                mySqlNode.getNode("database").getString(),
                mySqlNode.getNode("username").getString(),
                mySqlNode.getNode("password").getString());
    }

    /**
     * Creates a {@link uk.co.drnaylor.minecraft.hammer.core.HammerPluginActionProvider} for the {@link HammerCore}.
     * @return The {@link uk.co.drnaylor.minecraft.hammer.core.HammerPluginActionProvider}
     */
    private HammerPluginActionProvider createActionProvider() {
        return new HammerPluginActionProvider(
                new SpongePlayerActions(),
                new SpongeMessageSender(),
                new SpongePlayerToUUIDTranslator(),
                new SpongePlayerPermissionCheck(),
                new SpongeConfigurationProvider());
    }
}
