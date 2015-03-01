package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.List;
import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

public abstract class CommandCore {

    protected final HammerCore core;

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
}
