package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

public abstract class CommandCore {

    protected static final Format dateFormatter;

    protected static final ResourceBundle messageBundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    protected Collection<String> permissionNodes;

    protected final HammerCore core;

    static {
        dateFormatter = new SimpleDateFormat(messageBundle.getString("hammer.display.date"));
    }

    protected CommandCore(HammerCore core) {
        this.core = core;
    }

    protected abstract boolean requiresDatabase();

    /**
     * Executes the specific routines in this command core with the specified source.
     *
     * @param source The {@link WrappedCommandSource} that is executing the command.
     * @param arguments The arguments of the command
     * @param conn If the command requires database access, holds a {@link DatabaseConnection} object. Otherwise, null.
     * @return Whether the command succeeded
     * @throws HammerException Thrown if an exception is thrown in the command core.
     */
    protected abstract boolean executeCommand(WrappedCommandSource source, List<String> arguments, DatabaseConnection conn) throws HammerException;

    /**
     * Gets the usage of this command
     *
     * @return The {@link HammerText}
     */
    public abstract HammerText getUsageMessage();

    public final Collection<String> getRequiredPermissions() {
        return permissionNodes;
    }

    /**
     * Entry point into any command core
     *
     * @param source The {@link WrappedCommandSource} that is executing the command
     * @param arguments The arguments of the command
     * @return Whether the command succeeded
     * @throws HammerException Thrown if an exception is thrown in the command core
     */
    public final boolean executeCommand(WrappedCommandSource source, List<String> arguments) throws HammerException {
        // Permission check
        for (String p : this.getRequiredPermissions()) {
            if (!source.hasPermission(p)) {
                sendNoPermsMessage(source);
                return true;
            }
        }

        // Command execution.
        if (requiresDatabase()) {
            try (DatabaseConnection conn = core.getDatabaseConnection()) {
                return executeCommand(source, arguments, conn);
            } catch (HammerException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new HammerException("An unspecified error occurred", ex);
            }
        } else {
            return executeCommand(source, arguments, null);
        }
    }

    /**
     * Creates and sends a templated message
     *
     * @param player The {@link WrappedCommandSource} to send the message to
     * @param messageKey The message key in the resource bundle
     * @param isError Whether to create an error message or not
     * @param useStub Whether to use the [Hammer] tag
     * @param replacements The replacements in the templated message
     */
    protected final void sendTemplatedMessage(WrappedCommandSource player, String messageKey, boolean isError, boolean useStub, String... replacements) {
        sendMessage(player, MessageFormat.format(messageBundle.getString(messageKey), getFromStringArray(replacements)), isError, useStub);
    }

    /**
     * Sends a message to the {@link WrappedCommandSource}
     *
     * @param player The player to send a message to
     * @param message The message to send
     * @param isError Whether the message is an error
     * @param useStub Whether to use the [Hammer] tag
     */
    protected final void sendMessage(WrappedCommandSource player, String message, boolean isError, boolean useStub) {
        HammerTextBuilder hb;
        if (useStub) {
            hb = isError ? createErrorMessageStub() : createNormalMessageStub();
        } else {
            hb = new HammerTextBuilder();
        }

        hb.add(" " + message, isError ? HammerTextColours.RED : HammerTextColours.GREEN);
        player.sendMessage(hb.build());
    }

    /**
     * Sends the usage message to the player.
     *
     * @param uuid The {@link UUID} of the player
     */
    @Deprecated
    public final void sendUsageMessage(UUID uuid) {
        String f = String.format(" %s ", messageBundle.getString("hammer.player.commandUsage"));
        HammerTextBuilder hb = createErrorMessageStub().add(f, HammerTextColours.RED)
                .add(this.getUsageMessage());

        core.getActionProvider().getMessageSender().sendMessageToPlayer(uuid, hb.build());
    }

    /**
     * Sends the usage message to the {@link WrappedCommandSource}.
     *
     * @param source The source.
     */
    public final void sendUsageMessage(WrappedCommandSource source) {
        String f = String.format(" %s ", messageBundle.getString("hammer.player.commandUsage"));
        HammerTextBuilder hb = createErrorMessageStub().add(f, HammerTextColours.RED)
                .add(this.getUsageMessage());

        source.sendMessage(hb.build());
    }

    protected final void sendNoPlayerMessage(WrappedCommandSource target, String name) {
        sendTemplatedMessage(target, "hammer.player.noplayer", true, true, name);
    }

    protected final void sendNoPermsMessage(WrappedCommandSource target) {
        sendTemplatedMessage(target, "hammer.player.noperms", true, true);
    }

    private HammerTextBuilder createErrorMessageStub() {
        return new HammerTextBuilder().add(HammerConstants.textTag, HammerTextColours.RED);
    }

    private HammerTextBuilder createNormalMessageStub() {
        return new HammerTextBuilder().add(HammerConstants.textTag, HammerTextColours.GREEN);
    }

    private Object[] getFromStringArray(String[] s) {
        Object[] obj = new Object[s.length];
        for (int i = 0; i < s.length; i++) {
            obj[i] = s[i];
        }

        return obj;
    }
}
