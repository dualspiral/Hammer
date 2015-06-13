package uk.co.drnaylor.minecraft.hammer.bukkit.wrappers;

import org.bukkit.Server;
import uk.co.drnaylor.minecraft.hammer.bukkit.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

public class BukkitWrappedServer implements WrappedServer {

    private final Server server;

    public BukkitWrappedServer(Server server) {
        this.server = server;
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
}
