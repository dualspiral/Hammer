package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import com.google.common.base.Optional;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.sink.MessageSinks;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

import java.util.UUID;

public class SpongeWrappedServer implements WrappedServer {

    private final Game game;

    public SpongeWrappedServer(Game game) {
        this.game = game;
    }

    /**
     * Gets a player by the {@link UUID}
     *
     * @param uuid The {@link UUID}
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    @Override
    public WrappedPlayer getPlayer(UUID uuid) {
        Optional<Player> player = game.getServer().getPlayer(uuid);
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
        Optional<Player> player = game.getServer().getPlayer(name);
        if (player.isPresent()) {
            return new SpongeWrappedPlayer(game, player.get());
        }

        return null;
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
        MessageSinks.toPermission(permission).sendMessage(HammerTextConverter.constructMessage(message));
    }
}
