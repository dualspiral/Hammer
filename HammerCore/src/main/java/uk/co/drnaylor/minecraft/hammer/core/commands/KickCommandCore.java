package uk.co.drnaylor.minecraft.hammer.core.commands;

import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerPermissions;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

public class KickCommandCore extends CommandCore {

    public KickCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.kick");
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
        if (arguments.isEmpty()) {
            sendUsageMessage(source);
            return true;
        }

        Iterator<String> str = arguments.iterator();
        String arg = str.next();
        boolean quietKick = false;
        boolean noisyKick = false;

        // Do we want a quiet kick?
        if (arg.equalsIgnoreCase("-q") || arg.equalsIgnoreCase("-n")) {
            if (arg.equalsIgnoreCase("-q")) {
                quietKick = true;
            } else {
                noisyKick = true;
            }

            // No good being quiet if we don't have that person to kick!
            if (!str.hasNext()) {
                sendUsageMessage(source);
                return true;
            }

            arg = str.next();
        }

        // First argument - name. Second argument+, reason.
        // Get the player
        WrappedPlayer pl = core.getWrappedServer().getPlayer(arg);
        if (pl == null) {
            sendTemplatedMessage(source, "hammer.player.notonline", true, true);
            return true;
        }

        // Now, do we have any more arguments?
        String reason = "You have been kicked!";
        if (str.hasNext()) {
            // We do! Loop over, read the strings.
            StringBuilder sb = new StringBuilder();
            do {
                if (sb.length() > 0) {
                    sb.append(" ");
                }

                sb.append(str.next());
            } while (str.hasNext());
            reason = sb.toString();
        }

        pl.kick(reason);

        // Get the name of the person doing the kicking.
        String plName = source.getName();

        HammerText[] msg = createReasons(pl.getName(), plName, reason);
        if (noisyKick || (!quietKick && core.getWrappedServer().getConfiguration().getConfigBooleanValue("notifyAllOnBan"))) {
            for (HammerText m : msg) {
                core.getWrappedServer().sendMessageToServer(m);
            }
        } else {
            for (HammerText m : msg) {
                core.getWrappedServer().sendMessageToPermissionGroup(m, HammerPermissions.notify);
            }
        }

        return true;
    }

    @Override
    public HammerText getUsageMessage() {
        return new HammerTextBuilder().add("/kick name reason", HammerTextColours.YELLOW).build();
    }

    private HammerText[] createReasons(String playerKicked, String playerKicking, String reason) {
        HammerText[] t = new HammerText[2];
        t[0] = new HammerTextBuilder().add(HammerConstants.textTag + " ", HammerTextColours.RED).add(playerKicked, HammerTextColours.WHITE)
                .add(" " + messageBundle.getString("hammer.kick.kickMessage"), HammerTextColours.RED)
                .add(" " + playerKicking, HammerTextColours.WHITE).build();
        t[1] = new HammerTextBuilder().add(HammerConstants.textTag + " ", HammerTextColours.RED).add(MessageFormat.format(messageBundle.getString("hammer.kick.reason"), reason)
                , HammerTextColours.RED).build();
        return t;
    }
}
