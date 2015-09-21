package uk.co.drnaylor.minecraft.hammer.sponge;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.util.command.spec.CommandSpec;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerCoreFactory;
import uk.co.drnaylor.minecraft.hammer.core.commands.*;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerConnectListenerCore;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerJoinListenerCore;
import uk.co.drnaylor.minecraft.hammer.core.runnables.HammerPlayerUpdateRunnable;
import uk.co.drnaylor.minecraft.hammer.sponge.commands.HammerCommand;
import uk.co.drnaylor.minecraft.hammer.sponge.commands.SpongeCommand;
import uk.co.drnaylor.minecraft.hammer.sponge.listeners.PlayerConnectListener;
import uk.co.drnaylor.minecraft.hammer.sponge.listeners.PlayerJoinListener;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextToTextColorCoverter;
import uk.co.drnaylor.minecraft.hammer.sponge.wrappers.SpongeWrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.sponge.wrappers.SpongeWrappedServer;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Sponge plugin entrypoint
 */
@Plugin(id = "hammer", name = "Hammer for Sponge", version = HammerSponge.VERSION)
public class HammerSponge {

    public static final String VERSION = "0.2";

    @Inject private Game game;
    @Inject private Logger logger;
    @Inject @DefaultConfig(sharedRoot = false) private File defaultConfig;
    @Inject @DefaultConfig(sharedRoot = false) private ConfigurationLoader<CommentedConfigurationNode> configurationManager;

    private HammerCore core;
    private Task updateTask;
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
            HammerPlayerUpdateRunnable runnable = new HammerPlayerUpdateRunnable(core);

            // TODO: Register the service.

            logger.info("Establishing DB link and creating any missing tables...");
            try (DatabaseConnection conn = this.core.getDatabaseConnection()) {
                // Special case. We want true/false, not an exception here.
                if (!core.performStartupTasks(conn)) {
                    logger.error("Your DB credentials were rejected, or do not allow the required access to the database. Hammer will now disable itself.");
                    logger.error("-----------------------------------------------------------------");
                    return;
                }

                logger.info("Connection to DB was successful and all required tables were created.");
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

                logger.info("Registering Hammer events...");

                // Register the events
                game.getEventManager().registerListeners(this, new PlayerConnectListener(logger, game, new PlayerConnectListenerCore(core)));
                game.getEventManager().registerListeners(this, new PlayerJoinListener(game, new PlayerJoinListenerCore(runnable)));

                // Register server
                logger.info("Registering server ID group...");
                CommentedConfigurationNode configNode = configurationManager.load();
                conn.getServerHandler().updateServerNameForId(configNode.getNode("server", "id").getInt(), configNode.getNode("server", "name").getString("Unknown"));

                // Register the runnable.
                logger.info("Starting the async task...");

                updateTask = game.getScheduler().createTaskBuilder().async().interval(10, TimeUnit.SECONDS).execute(runnable).submit(this);
            }
        } catch (Exception ex) {
            logger.error("A fatal error has occurred. Hammer will now disable itself.");
            logger.error("Here. Have a stack trace to tell you why!");
            ex.printStackTrace();
            logger.info("-----------------------------------------------------------------");
            return;
        }

        isLoaded = true;
        logger.info("Hammer has successfully initialised and is managing your bans.");
        logger.info("-----------------------------------------------------------------");
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
            updateTask.cancel();
            updateTask.getRunnable().run();
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
     * Gets the configuration manager for Hammer.
     *
     * @return The {@link ConfigurationLoader}
     */
    public ConfigurationLoader<CommentedConfigurationNode> getConfigurationManager() {
        return this.configurationManager;
    }

    /**
     * Creates the {@link HammerCore} object.
     * @throws ClassNotFoundException The MySQL JDBC driver isn't on the classpath.
     * @throws IOException Configuration could not be loaded.
     */
    private void createCore() throws ClassNotFoundException, IOException {
        // Create the path if it does not exist.
        if (!defaultConfig.exists()) {
            defaultConfig.getParentFile().mkdirs();
            defaultConfig.createNewFile();
        }

        CommentedConfigurationNode configNode = configurationManager.load();
        if (configNode.getChildrenMap().isEmpty()) {
            createConfig(configNode);
        }

        CommentedConfigurationNode mySqlNode = configNode.getNode("mysql");
        core = HammerCoreFactory.CreateHammerCoreWithMySQL(
                new SpongeWrappedServer(this, game),
                mySqlNode.getNode("host").getString(),
                mySqlNode.getNode("port").getInt(),
                mySqlNode.getNode("database").getString(),
                mySqlNode.getNode("username").getString(),
                mySqlNode.getNode("password").getString());
    }

    private void createConfig(CommentedConfigurationNode node) throws IOException {
        node.getNode("mysql", "host").setValue("localhost").setComment("The location of the database");
        node.getNode("mysql", "port").setValue(3306).setComment("The port for the database");
        node.getNode("mysql", "database").setValue("hammer").setComment("The name of the database to connect to.");
        node.getNode("mysql", "username").setValue("username").setComment("The username for the database connection");
        node.getNode("mysql", "password").setValue("password").setComment("The password for the database connection");
        node.getNode("server", "id").setValue(1).setComment("A unique integer id to represent this server");
        node.getNode("server", "name").setValue("New Server").setComment("A display name for this server when using Hammer");
        node.getNode("notifyAllOnBan").setValue(true).setComment("If set to false, only those with the 'hammer.notify' permission will be notified when someone is banned.");
        configurationManager.save(node);
    }
}
