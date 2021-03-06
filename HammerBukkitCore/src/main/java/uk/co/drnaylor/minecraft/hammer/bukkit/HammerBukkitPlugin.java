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
package uk.co.drnaylor.minecraft.hammer.bukkit;

import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.DumperOptions;
import uk.co.drnaylor.minecraft.hammer.bukkit.commands.BukkitAlias;
import uk.co.drnaylor.minecraft.hammer.bukkit.commands.BukkitCommand;
import uk.co.drnaylor.minecraft.hammer.bukkit.commands.HammerCommand;
import uk.co.drnaylor.minecraft.hammer.bukkit.listeners.PlayerConnectListener;
import uk.co.drnaylor.minecraft.hammer.bukkit.listeners.PlayerJoinListener;
import uk.co.drnaylor.minecraft.hammer.bukkit.wrappers.BukkitWrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.bukkit.wrappers.BukkitWrappedServer;
import uk.co.drnaylor.minecraft.hammer.core.HammerConfiguration;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.*;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class HammerBukkitPlugin extends JavaPlugin {

    private static final String filePath = "plugins/Hammer/config.yml";
    private HammerCore core;
    private static HammerBukkitPlugin instance;

    public Collection<? extends Player> getOnlinePlayers() {
        return this.getServer().getOnlinePlayers();
    }

    public static HammerBukkitPlugin getPlugin() {
        return instance;
    }

    /**
     * Runs when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        instance = this;
        this.getLogger().log(Level.INFO, "-----------------------------------------------------------------");
        this.getLogger().log(Level.INFO, "Welcome to Hammer for Bukkit version {0}", this.getDescription().getVersion());
        this.getLogger().log(Level.INFO, "Hammer will now perform some startup tasks. Stand by...");

        // Lots can go wrong here. If anything does go wrong, the
        // plugin heads straight for the disabled pile... once it's
        // exploded all over the console.
        try {
            this.getLogger().log(Level.INFO, "Loading Hammer configuration.");
            createCore(this.getConfigurateConfig());

            try (DatabaseConnection conn = this.core.getDatabaseConnection()) {
                // Special case. We want true/false, not an exception here.
                if (!core.performStartupTasks(conn)) {
                    this.getPluginLoader().disablePlugin(this);
                    return;
                }

                this.getLogger().log(Level.INFO, "Registering Hammer commands...");

                // Ban command
                this.getCommand("ban").setExecutor(new BukkitCommand(new BanCommandCore(core)));
                this.getCommand("tempban").setExecutor(new BukkitCommand(new TempBanCommandCore(core)));
                this.getCommand("unban").setExecutor(new BukkitCommand(new UnbanCommandCore(core)));
                this.getCommand("checkban").setExecutor(new BukkitCommand(new CheckBanCommandCore(core)));

                // Kick commands
                this.getCommand("kick").setExecutor(new BukkitCommand(new KickCommandCore(core)));
                this.getCommand("kickall").setExecutor(new BukkitCommand(new KickAllCommandCore(core)));

                // Hammer
                this.getCommand("hammer").setExecutor(new HammerCommand(this));
                this.getCommand("hammerreload").setExecutor(new BukkitCommand(new ReloadCommandCore(core)));

                // Import player
                this.getCommand("importplayer").setExecutor(new BukkitCommand(new ImportPlayerCommand(core)));

                // Upgrade to perm
                this.getCommand("toperm").setExecutor(new BukkitCommand(new UpgradeToPermBanCommandCore(core)));
                this.getCommand("updatebans").setExecutor(new BukkitCommand(new UpdateBansCommandCore(core)));

                ArrayList<String> arguments = new ArrayList<>();
                arguments.add("-a");
                this.getCommand("gban").setExecutor(new BukkitAlias(this, "ban", arguments));

                ArrayList<String> arguments1 = new ArrayList<>();
                arguments1.add("-p");
                this.getCommand("permban").setExecutor(new BukkitAlias(this, "ban", arguments1));
                this.getCommand("banip").setExecutor(new BukkitCommand(new BanIPCommandCore(core)));
                this.getCommand("tempbanip").setExecutor(new BukkitCommand(new TempBanIPCommandCore(core)));
                this.getCommand("ipunban").setExecutor(new BukkitCommand(new UnbanIPCommandCore(core)));

                this.getCommand("ipunban").setExecutor(new BukkitCommand(new UnbanIPCommandCore(core)));

                this.getCommand("importserverbans").setExecutor(new BukkitCommand(new ImportServerBansCommandCore(core)));

                this.getLogger().log(Level.INFO, "Registering Hammer events...");
                this.getServer().getPluginManager().registerEvents(new PlayerConnectListener(this), this);

                // TODO: Better registration.
                this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(core.getPlayerJoinListenerCore()), this);

                // Are players online? If so, we've gone mid run and need to save a load of players.
                if (!this.getOnlinePlayers().isEmpty()) {
                    this.getLogger().log(Level.INFO, "Players are currently online. Ensuring the players are registered in Hammer...");

                    List<HammerPlayerInfo> i = getOnlinePlayers().stream().map(player -> new BukkitWrappedPlayer(player).getHammerPlayer()).collect(Collectors.toList());
                    conn.getPlayerHandler().updatePlayers(i);
                }
            }

            core.postInit();
        } catch (Exception e) {
            this.getLogger().severe("A fatal error has occurred. Hammer will now disable itself.");
            this.getLogger().severe("Here. Have a stack trace to tell you why!");
            e.printStackTrace();
            this.getLogger().log(Level.INFO, "-----------------------------------------------------------------");
            this.getPluginLoader().disablePlugin(this);
            return;
        }
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

    private HammerConfiguration getConfigurateConfig() throws IOException {
        File fp = new File(filePath);
        if (!fp.exists()) {
            fp.getParentFile().mkdirs();
            fp.createNewFile();
        }

        YAMLConfigurationLoader.Builder cl = YAMLConfigurationLoader.builder().setIndent(4).setFlowStyle(DumperOptions.FlowStyle.BLOCK);
        cl.setFile(fp);
        return new HammerConfiguration(cl.build());
    }

    /**
     * Creates the {@link HammerCore} object.
     * @param config the {@link HammerConfiguration} that contains the config.
     * @throws ClassNotFoundException The database driver isn't on the classpath.
     */
    private void createCore(HammerConfiguration config) throws ClassNotFoundException, HammerException {
        core = new HammerCore(
                new BukkitWrappedServer(this, this.getServer()),
                config);
    }
}
