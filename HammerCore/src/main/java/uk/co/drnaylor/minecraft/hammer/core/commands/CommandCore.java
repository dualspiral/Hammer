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
     * Executes this command core with the specified player.
     * @param playerUUID
     * @param arguments 
     * @param isConsole Whether the command executor is the console.
     * @param conn If the command requires database access, holds a {@link DatabaseConnection} object. Otherwise, null.
     * @return Whether the command succeeded.
     */
    protected abstract boolean executeCommand(UUID playerUUID, List<String> arguments, boolean isConsole, DatabaseConnection conn) throws HammerException;

    /**
     * Gets the usage of this command
     *
     * @return The {@link HammerText}
     */
    public abstract HammerText getUsageMessage();

    public final Collection<String> getRequiredPermissions() {
        return permissionNodes;
    }

    private boolean executeCommand(UUID playerUUID, List<String> arguments, boolean isConsole) throws HammerException {
        if (requiresDatabase()) {
            try (DatabaseConnection conn = core.getDatabaseConnection()) {
                return executeCommand(playerUUID, arguments, isConsole, conn);
            } catch (HammerException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new HammerException("An unspecified error occured", ex);
            }
        } else {
            return executeCommand(playerUUID, arguments, isConsole, null);
        }
    }

    /**
     * Executes this command core with the specified player.
     * @param playerUUID
     * @param arguments 
     * @return Whether the command succeeded.
     */
    public final boolean executeCommandAsPlayer(UUID playerUUID, List<String> arguments) throws HammerException {
        // If the player does not have permission, stop them here.
        for (String p : this.getRequiredPermissions()) {
            if (core.getActionProvider().getPermissionCheck().hasPermission(playerUUID, p)) {
                sendNoPermsMessage(playerUUID);
                return true;
            }
        }

        // The player is the console if we've sent down the console UUID.
        return executeCommand(playerUUID, arguments, playerUUID.equals(HammerConstants.consoleUUID));
    }

    /**
     * Executes this command core as the console.
     * @param arguments The arguments to the command.
     * @return Whether the command succeeded.
     */
    public final boolean executeCommandAsConsole(List<String> arguments) throws HammerException {
        return executeCommand(HammerConstants.consoleUUID, arguments, true);
    }

    protected final void sendTemplatedMessage(UUID uuid, String messageKey, boolean isError, boolean useStub, String... replacements) {
        sendMessage(uuid, MessageFormat.format(messageBundle.getString(messageKey), getFromStringArray(replacements)), isError, useStub);
    }

    protected final void sendMessage(UUID uuid, String message, boolean isError, boolean useStub) {
        HammerTextBuilder hb;
        if (useStub) {
            hb = isError ? createErrorMessageStub() : createNormalMessageStub();
        } else {
            hb = new HammerTextBuilder();
        }

        hb.add(" " + message, isError ? HammerTextColours.RED : HammerTextColours.GREEN);
        if (uuid.equals(HammerConstants.consoleUUID)) {
            core.getActionProvider().getMessageSender().sendMessageToConsole(hb.build());
        } else {
            core.getActionProvider().getMessageSender().sendMessageToPlayer(uuid, hb.build());
        }
    }

    /**
     * Sends the usage message to the player.
     *
     * @param uuid The {@link UUID} of the player
     */
    public final void sendUsageMessage(UUID uuid) {
        String f = String.format(" %s ", messageBundle.getString("hammer.player.commandUsage"));
        HammerTextBuilder hb = createErrorMessageStub().add(f, HammerTextColours.RED)
                .add(this.getUsageMessage());

        core.getActionProvider().getMessageSender().sendMessageToPlayer(uuid, hb.build());
    }

    protected final void sendNoPlayerMessage(UUID uuid, String name) {
        sendTemplatedMessage(uuid, "hammer.player.noplayer", true, true, name);
    }

    protected final void sendNoPermsMessage(UUID uuid) {
        sendTemplatedMessage(uuid, "hammer.player.noperms", true, true);
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
