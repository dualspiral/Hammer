package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorage;
import org.spongepowered.api.text.sink.MessageSinks;
import org.spongepowered.api.util.command.CommandSource;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedConfiguration;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpongeWrappedServer implements WrappedServer {

    private final Game game;
    private final HammerSponge plugin;
    private final SpongeWrappedConfiguration config;

    public SpongeWrappedServer(HammerSponge plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
        this.config = new SpongeWrappedConfiguration(plugin, game);
    }

    /**
     * Gets a player by the {@link UUID}
     *
     * @param uuid The {@link UUID}
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    @Override
    public WrappedPlayer getPlayer(UUID uuid) {
        UserStorage service = game.getServiceManager().provide(UserStorage.class).get();
        Optional<User> player = service.get(uuid);
        if (player.isPresent()) {
            return new SpongeWrappedPlayer(game, player.get());
        }

        return null;
    }

    /**
     * Gets a player by their last known name
     *
     * @param name The name
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    @Override
    public WrappedPlayer getPlayer(String name) {
        UserStorage service = game.getServiceManager().provide(UserStorage.class).get();
        Optional<User> player = service.get(name);
        if (player.isPresent()) {
            return new SpongeWrappedPlayer(game, player.get());
        }

        return null;
    }

    /**
     * Gets the console command sender.
     *
     * @return The {@link WrappedCommandSource} that represents the console.
     */
    public WrappedCommandSource getConsole() {
        return new SpongeWrappedConsole(game.getServer().getConsole());
    }

    /**
     * Sends a message to the entire server.
     *
     * @param message The message to send.
     */
    @Override
    public void sendMessageToServer(HammerText message) {
        game.getServer().getBroadcastSink().sendMessage(HammerTextConverter.constructMessage(message));
    }

    /**
     * Sends a message to a permission group.
     *
     * @param message    The message to send.
     * @param permission The permission group that should see it.
     */
    @Override
    public void sendMessageToPermissionGroup(HammerText message, String permission) {
        // Get the players with the permission group.
        Set<CommandSource> targets = game.getServer().getOnlinePlayers().stream().filter(p -> p.hasPermission(permission)).collect(Collectors.toSet());

        // Add the console.
        targets.add(game.getServer().getConsole());

        // Send
        MessageSinks.to(targets).sendMessage(HammerTextConverter.constructMessage(message));
    }

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    @Override
    public void kickAllPlayers(WrappedCommandSource source, String reason) {
        kickAllPlayers(source, new HammerTextBuilder().add(reason).build());
    }

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    @Override
    public void kickAllPlayers(WrappedCommandSource source, HammerText reason) {
        // Copy the collection so that we can operate on it, in case the implemenation removes
        // players from the collection as we kick them.
        Collection<Player> players = new ArrayList<>(game.getServer().getOnlinePlayers());

        // We use a while in case the loop has been mutated.
        for (Player player : players) {
            if (player.getUniqueId() == source.getUUID()) {
                // Don't kick the current player.
                continue;
            }

            player.kick(HammerTextConverter.constructMessage(reason));
        }
    }

    /**
     * Schedules an action for the next tick loop.
     *
     * @param runnable The runnable to run on the next tick loop.
     */
    @Override
    public void scheduleForNextTick(Runnable runnable) {
        game.getScheduler().createTaskBuilder().delay(0).execute(runnable).submit(plugin);
    }

    /**
     * Gets a object that contains methods for obtaining configuration notes.
     *
     * @return Gets a {@link WrappedConfiguration} object.
     */
    @Override
    public WrappedConfiguration getConfiguration() {
        return config;
    }
}
