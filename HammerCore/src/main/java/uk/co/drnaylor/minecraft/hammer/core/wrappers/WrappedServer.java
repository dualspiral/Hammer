package uk.co.drnaylor.minecraft.hammer.core.wrappers;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

import java.util.UUID;

public interface WrappedServer {

    /**
     * Gets a player by the {@link UUID}
     *
     * @param uuid The {@link UUID}
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    WrappedPlayer getPlayer(UUID uuid);

    /**
     * Gets a player by their last known name
     *
     * @param name The name
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    WrappedPlayer getPlayer(String name);

    /**
     * Sends a message to the entire server.
     *
     * @param message The message to send.
     */
    void sendMessageToServer(HammerText message);

    /**
     * Sends a message to a permission group.
     *
     * @param message The message to send.
     * @param permission The permission group that should see it.
     */
    void sendMessageToPermissionGroup(HammerText message, String permission);
}
