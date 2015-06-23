package uk.co.drnaylor.minecraft.hammer.sponge;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.InitializationEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.util.command.spec.CommandSpec;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerCoreFactory;
import uk.co.drnaylor.minecraft.hammer.core.HammerPluginActionProvider;
import uk.co.drnaylor.minecraft.hammer.core.commands.*;
import uk.co.drnaylor.minecraft.hammer.sponge.commands.HammerCommand;
import uk.co.drnaylor.minecraft.hammer.sponge.commands.SpongeCommand;
import uk.co.drnaylor.minecraft.hammer.sponge.coreimpl.*;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextToTextColorCoverter;
import uk.co.drnaylor.minecraft.hammer.sponge.wrappers.SpongeWrappedServer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Sponge plugin entrypoint
 */
@Plugin(id = "hammersponge", name = "Hammer for Sponge", version = HammerSponge.VERSION)
public class HammerSponge {

    public static final String VERSION = "0.2";

    @Inject private Game game;
    @Inject private Logger logger;
    @Inject @DefaultConfig(sharedRoot = false) private File defaultConfig;
    @Inject @DefaultConfig(sharedRoot = false) private ConfigurationLoader<CommentedConfigurationNode> configurationManager;

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

            // Register the commands
            logger.info("Registering Hammer commands...");

            CommandSpec spec = CommandSpec.builder().executor(new HammerCommand(this)).build();
            game.getCommandDispatcher().register(this, spec, "hammer");

            // Ban command
            game.getCommandDispatcher().register(this, new SpongeCommand(game, new BanCommandCore(core)), "ban", "hban", "hammerban");
            game.getCommandDispatcher().register(this, new SpongeCommand(game, new TempBanCommandCore(core)), "tempban", "tban", "htban", "hammertban");
            game.getCommandDispatcher().register(this, new SpongeCommand(game, new PermBanCommandCore(core)), "permban", "hammerpban", "hpban", "pban");
            game.getCommandDispatcher().register(this, new SpongeCommand(game, new UnbanCommandCore(core)), "unban", "hunban", "hammerunban");
            game.getCommandDispatcher().register(this, new SpongeCommand(game, new CheckBanCommandCore(core)), "checkban", "hcheckban", "hammercheckban");

            // Kick commands
            game.getCommandDispatcher().register(this, new SpongeCommand(game, new KickCommandCore(core)), "kick", "hkick", "hammerkick");
            game.getCommandDispatcher().register(this, new SpongeCommand(game, new KickAllCommandCore(core)), "kickall", "hkickall", "hammerkickall");

            // Import Player command
            game.getCommandDispatcher().register(this, new SpongeCommand(game, new ImportPlayerCommand(core)), "importplayer", "himportplayer");
        } catch (Exception ex) {
            // Do some stuff
        }
    }

    /**
     * Runs when the server is starting.
     *
     * @param event The event
     */
    @Subscribe
    public void onServerStarting(ServerStartingEvent event) {
        // Once the server is starting, reset the text colour map.
        HammerTextToTextColorCoverter.init();
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
                new SpongeWrappedServer(this, game),
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
                new SpongePlayerTranslator(),
                new SpongeConfigurationProvider());
    }
}
