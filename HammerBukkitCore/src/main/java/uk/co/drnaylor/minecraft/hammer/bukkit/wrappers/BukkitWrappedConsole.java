package uk.co.drnaylor.minecraft.hammer.bukkit.wrappers;

import org.bukkit.command.ConsoleCommandSender;
import uk.co.drnaylor.minecraft.hammer.bukkit.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.util.UUID;

/**
 * Represents a wrapped {@link ConsoleCommandSender}.
 */
public class BukkitWrappedConsole implements WrappedCommandSource {

    private final ConsoleCommandSender sender;

    public BukkitWrappedConsole(ConsoleCommandSender sender) {
        this.sender = sender;
    }

    /**
     * Gets the name of the source.
     *
     * @return The name.
     */
    @Override
    public String getName() {
        return HammerConstants.consoleName;
    }

    /**
     * Gets the Unique Identifier of the source.
     *
     * @return The {@link UUID}
     */
    @Override
    public UUID getUUID() {
        return HammerConstants.consoleUUID;
    }

    /**
     * Sends a message to the target
     *
     * @param message The message
     */
    @Override
    public void sendMessage(HammerText message) {
        sender.sendMessage(HammerTextConverter.constructMessage(message));
    }

    /**
     * Sends a message to the target
     *
     * @param message The message
     */
    @Override
    public void sendMessage(String message) {
        sender.sendMessage(message);
    }

    /**
     * Gets whether the source has the specified permission
     *
     * @param permission The permission
     * @return <code>true</code> if the source has the permission specified.
     */
    @Override
    public boolean hasPermission(String permission) {
        return true;
    }
}
