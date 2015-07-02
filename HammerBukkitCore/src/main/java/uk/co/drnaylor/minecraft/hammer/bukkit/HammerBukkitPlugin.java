package uk.co.drnaylor.minecraft.hammer.bukkit;

import uk.co.drnaylor.minecraft.hammer.bukkit.commands.BukkitAlias;
import uk.co.drnaylor.minecraft.hammer.bukkit.commands.BukkitCommand;
import uk.co.drnaylor.minecraft.hammer.bukkit.commands.HammerCommand;
import uk.co.drnaylor.minecraft.hammer.bukkit.coreimpl.*;
import uk.co.drnaylor.minecraft.hammer.bukkit.listeners.PlayerJoinListener;
import uk.co.drnaylor.minecraft.hammer.bukkit.runnables.CreateHammerPlayerRunnable;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import uk.co.drnaylor.minecraft.hammer.bukkit.listeners.PlayerConnectListener;
import uk.co.drnaylor.minecraft.hammer.bukkit.wrappers.BukkitWrappedServer;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerCoreFactory;
import uk.co.drnaylor.minecraft.hammer.core.HammerPluginActionProvider;
import uk.co.drnaylor.minecraft.hammer.core.commands.*;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

import java.util.ArrayList;
import java.util.logging.Level;

public abstract class HammerBukkitPlugin extends JavaPlugin {

    protected HammerCore core;
    protected CreateHammerPlayerRunnable runnable;

    public abstract Player[] getOnlinePlayers();

    /**
     * Runs when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "-----------------------------------------------------------------");
        this.getLogger().log(Level.INFO, "Welcome to Hammer for Bukkit version {0}", this.getDescription().getVersion());
        this.getLogger().log(Level.INFO, "Hammer will now perform some startup tasks. Stand by...");
        this.saveDefaultConfig();
        this.reloadConfig();

        // Lots can go wrong here. If anything does go wrong, the
        // plugin heads straight for the disabled pile... once it's
        // exploded all over the console.
        try {
            createCore();
            this.getLogger().log(Level.INFO, "Loading Hammer Core version {0}", core.getHammerCoreVersion());

            this.getLogger().log(Level.INFO, "Establishing DB link and creating any missing tables...");
            try (DatabaseConnection conn = this.core.getDatabaseConnection()) {
                // Special case. We want true/false, not an exception here.
                if (!core.performStartupTasks(conn)) {
                    this.getLogger().severe("Your DB credentials were rejected, or do not allow the required access to the database. Hammer will now disable itself.");
                    this.getLogger().log(Level.INFO, "-----------------------------------------------------------------");
                    this.getPluginLoader().disablePlugin(this);
                    return;
                }

                this.getLogger().log(Level.INFO, "Connection to DB was successful and all required tables were created.");
                this.getLogger().log(Level.INFO, "Registering Hammer commands...");

                // Ban command
                this.getCommand("ban").setExecutor(new BukkitCommand(new BanCommandCore(core)));
                this.getCommand("tempban").setExecutor(new BukkitCommand(new TempBanCommandCore(core)));
                this.getCommand("permban").setExecutor(new BukkitCommand(new PermBanCommandCore(core)));
                this.getCommand("unban").setExecutor(new BukkitCommand(new UnbanCommandCore(core)));
                this.getCommand("checkban").setExecutor(new BukkitCommand(new CheckBanCommandCore(core)));

                // Kick commands
                this.getCommand("kick").setExecutor(new BukkitCommand(new KickCommandCore(core)));
                this.getCommand("kickall").setExecutor(new BukkitCommand(new KickAllCommandCore(core)));

                // Hammer
                this.getCommand("hammer").setExecutor(new HammerCommand(this));

                // Import player
                this.getCommand("importplayer").setExecutor(new BukkitCommand(new ImportPlayerCommand(core)));

                ArrayList<String> arguments = new ArrayList<>();
                arguments.add("-a");
                this.getCommand("gban").setExecutor(new BukkitAlias(this, "ban", arguments));
                // this.getCommand("banip").setExecutor(new BukkitCommand(new BanIPCommandCore(core)));
                // this.getCommand("unbanip").setExecutor(new BukkitCommand(new UnbanIPCommandCore(core)));

                this.getLogger().log(Level.INFO, "Registering Hammer events...");
                this.getServer().getPluginManager().registerEvents(new PlayerConnectListener(this), this);
                this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

                // Register server
                this.getLogger().log(Level.INFO, "Registering server ID group...");
                conn.getServerHandler().updateServerNameForId(this.getConfig().getInt("server.id"), this.getConfig().getString("server.name"));

                // Are players online? If so, we've gone mid run and need to save a load of players.
                if (this.getOnlinePlayers().length > 0) {
                    this.getLogger().log(Level.INFO, "Players are currently online. Ensuring the players are registered in Hammer...");
                    conn.getPlayerHandler().updatePlayers(BukkitHammerPlayerTranslator.getHammerPlayers(getOnlinePlayers()));
                }
            }

            // Start the scheduled task...
            this.getLogger().info("Starting the async task...");
            this.runnable = new CreateHammerPlayerRunnable(this);
            this.getServer().getScheduler().runTaskTimerAsynchronously(this, runnable, 600l, 600l);

        } catch (Exception e) {
            this.getLogger().severe("A fatal error has occurred. Hammer will now disable itself.");
            this.getLogger().severe("Here. Have a stack trace to tell you why!");
            e.printStackTrace();
            this.getLogger().log(Level.INFO, "-----------------------------------------------------------------");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        this.getLogger().log(Level.INFO, "Hammer has successfully initialised and is managing your bans.");
        this.getLogger().log(Level.INFO, "-----------------------------------------------------------------");
    }

    /**
     * Runs when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        this.getLogger().info("Hammer is now being disabled and will no longer manage your bans.");
    }

    public HammerCore getHammerCore() {
        return core;
    }

    public void addToHammerPlayerRunnable(Player p) {
        this.runnable.addPlayer(p);
    }

    /**
     * Creates the {@link HammerCore} object.
     * @throws ClassNotFoundException The MySQL JDBC drive isn't on the classpath.
     */
    protected final void createCore() throws ClassNotFoundException {
        Configuration config = this.getConfig();
        core = HammerCoreFactory.CreateHammerCoreWithMySQL(
                new BukkitWrappedServer(this, this.getServer()),
                config.getString("mysql.host"),
                config.getInt("mysql.port"),
                config.getString("mysql.database"),
                config.getString("mysql.username"),
                config.getString("mysql.password"));
    }
}
