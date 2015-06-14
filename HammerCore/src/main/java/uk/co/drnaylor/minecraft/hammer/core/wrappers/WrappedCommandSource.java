package uk.co.drnaylor.minecraft.hammer.core.wrappers;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

import java.util.UUID;

public interface WrappedCommandSource {
    /**
     * Gets the name of the source.
     *
     * @return The name.
     */
    String getName();

    /**
     * Gets the Unique Identifier of the source.
     *
     * @return The {@link UUID}
     */
    UUID getUUID();

    /**
     * Sends a message to the target
     *
     * @param message The message
     */
    void sendMessage(HammerText message);

    /**
     * Sends a message to the target
     *
     * @param message The message
     */
    void sendMessage(String message);

    /**
     * Gets whether the source has the specified permission
     *
     * @param permission The permission
     * @return <code>true</code> if the source has the permission specified.
     */
    boolean hasPermission(String permission);
}
