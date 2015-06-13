package uk.co.drnaylor.minecraft.hammer.core.wrappers;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

public interface WrappedServer {

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
