package uk.co.drnaylor.minecraft.hammer.core.commands;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.util.List;

public class KickAllCommandCore extends CommandCore {

    public KickAllCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.kickall");
    }

    @Override
    protected boolean requiresDatabase() {
        return false;
    }

    /**
     * Executes the specific routines in this command core with the specified source.
     *
     * @param source    The {@link WrappedCommandSource} that is executing the command.
     * @param arguments The arguments of the command
     * @param conn      If the command requires database access, holds a {@link DatabaseConnection} object. Otherwise, null.
     * @return Whether the command succeeded
     * @throws HammerException Thrown if an exception is thrown in the command core.
     */
    @Override
    protected boolean executeCommand(WrappedCommandSource source, List<String> arguments, DatabaseConnection conn) throws HammerException {
        String reason = "You have all been kicked from the server.";
        if (!arguments.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String s : arguments) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }

                sb.append(s);
            }

            reason = sb.toString();
        }

        core.getWrappedServer().kickAllPlayers(source, reason);
        sendTemplatedMessage(source, "hammer.kickall", false, true);
        return true;
    }

    @Override
    public HammerText getUsageMessage() {
        return new HammerTextBuilder().add("/kickall [reason]", HammerTextColours.YELLOW).build();
    }
}
