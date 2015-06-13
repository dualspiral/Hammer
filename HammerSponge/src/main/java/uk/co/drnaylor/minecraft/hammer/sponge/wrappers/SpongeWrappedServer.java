package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import org.spongepowered.api.Game;
import org.spongepowered.api.text.sink.MessageSinks;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

public class SpongeWrappedServer implements WrappedServer {

    private final Game game;

    public SpongeWrappedServer(Game game) {
        this.game = game;
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
