package uk.co.drnaylor.minecraft.hammer.bukkit.wrappers;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;
import uk.co.drnaylor.minecraft.hammer.bukkit.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.*;

import java.util.*;

public class BukkitWrappedServer implements WrappedServer {

    private final Server server;
    private final HammerBukkitPlugin plugin;
    private final WrappedConfiguration config;
    private final WrappedScheduler scheduler;

    public BukkitWrappedServer(HammerBukkitPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
        this.config = new BukkitWrappedConfiguration(plugin);
        this.scheduler = new BukkitWrappedScheduler(plugin);
    }

    /**
     * Gets a player by the {@link UUID}
     *
     * @param uuid The {@link UUID}
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    @Override
    public WrappedPlayer getPlayer(UUID uuid) {
        return BukkitWrappedPlayer.of(uuid);
    }

    /**
     * Gets a player by their last known name
     *
     * @param name The name
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    @Override
    public WrappedPlayer getPlayer(String name) {
        return BukkitWrappedPlayer.ofOnlinePlayer(name);
    }

    /**
     * Gets the console command sender.
     *
     * @return The {@link WrappedCommandSource} that represents the console.
     */
    public WrappedCommandSource getConsole() {
        return new BukkitWrappedConsole(server.getConsoleSender());
    }

    /**
     * Sends a message to the entire server.
     *
     * @param message The message to send.
     */
    @Override
    public void sendMessageToServer(HammerText message) {
        server.broadcastMessage(HammerTextConverter.constructMessage(message));
    }

    /**
     * Sends a message to a permission group.
     *
     * @param message    The message to send.
     * @param permission The permission group that should see it.
     */
    @Override
    public void sendMessageToPermissionGroup(HammerText message, String permission) {
        server.broadcast(HammerTextConverter.constructMessage(message), permission);
    }

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    @Override
    public void kickAllPlayers(WrappedCommandSource source, String reason) {
        List<Player> lpl = Arrays.asList(plugin.getOnlinePlayers());
        Iterator<Player> iterator = lpl.iterator();

        // Note that we are using an iterator here as we cannot guarantee that the player remains in the
        // list of online players.
        while (iterator.hasNext()) {
            Player pl = iterator.next();
            if (pl.isOnline() && source.getUUID() != pl.getUniqueId()) {
                pl.kickPlayer(ChatColor.translateAlternateColorCodes('&', reason));
            }
        }
    }

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    @Override
    public void kickAllPlayers(WrappedCommandSource source, HammerText reason) {
        kickAllPlayers(source, HammerTextConverter.constructMessage(reason));
    }

    /**
     * Gets a object that contains methods for obtaining configuration notes.
     *
     * @return Gets a {@link WrappedConfiguration} object.
     */
    @Override
    public WrappedConfiguration getConfiguration() {
        return this.config;
    }

    @Override
    public WrappedScheduler getScheduler() {
        return this.scheduler;
    }
}
