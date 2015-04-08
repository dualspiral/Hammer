package uk.co.drnaylor.minecraft.hammer.core.commands;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

import java.util.List;
import java.util.UUID;

public class KickAllCommandCore extends CommandCore {

    public KickAllCommandCore(HammerCore core) {
        super(core);
    }

    @Override
    protected boolean requiresDatabase() {
        return false;
    }

    /**
     * Executes this command core with the specified player.
     *
     * @param playerUUID
     * @param arguments
     * @param isConsole  Whether the command executor is the console.
     * @param conn       If the command requires database access, holds a {@link DatabaseConnection} object. Otherwise, null.
     * @return Whether the command succeeded.
     */
    @Override
    protected boolean executeCommand(UUID playerUUID, List<String> arguments, boolean isConsole, DatabaseConnection conn) throws HammerException {
        String reason = "You have all been kicked from the server.";
        if (!arguments.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String s : arguments) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }

                sb.append(s);
            }
        }

        core.getActionProvider().getPlayerActions().kickAllPlayers(playerUUID, reason);
        sendTemplatedMessage(playerUUID, "hammer.kickall", false, true);
        return true;
    }
}
