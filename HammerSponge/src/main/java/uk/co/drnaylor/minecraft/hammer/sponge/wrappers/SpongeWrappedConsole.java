package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.source.ConsoleSource;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

import java.util.UUID;

public class SpongeWrappedConsole implements WrappedCommandSource {

    private final ConsoleSource console;

    public SpongeWrappedConsole(ConsoleSource console) {
        this.console = console;
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
        console.sendMessage(HammerTextConverter.constructMessage(message));
    }

    /**
     * Sends a message to the target
     *
     * @param message The message
     */
    @Override
    public void sendMessage(String message) {
        console.sendMessage(Texts.of(message));
    }

    /**
     * Gets whether the source has the specified permission
     *
     * @param permission The permission
     * @return <code>true</code> if the source has the permission specified.
     */
    @Override
    public boolean hasPermission(String permission) {
        // The console always has permission.
        return true;
    }

}
