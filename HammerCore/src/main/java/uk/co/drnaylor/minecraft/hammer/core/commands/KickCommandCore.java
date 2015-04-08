package uk.co.drnaylor.minecraft.hammer.core.commands;

import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class KickCommandCore extends CommandCore {

    public KickCommandCore(HammerCore core) {
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
        if (arguments.isEmpty()) {
            sendUsageMessage(playerUUID, "/kick [-q] <name> [message]");
            return true;
        }

        Iterator<String> str = arguments.iterator();
        String arg = str.next();
        boolean quietKick = false;

        // Do we want a quiet kick?
        if (arg.equalsIgnoreCase("-q")) {
            quietKick = true;

            // No good being quiet if we don't have that person to kick!
            if (!str.hasNext()) {
                sendUsageMessage(playerUUID, "/kick [-q] <name> [message]");
                return true;
            }

            arg = str.next();
        }

        // First argument - name. Second argument+, reason.
        // Get the player
        WrappedPlayer pl = core.getActionProvider().getPlayerTranslator().nameToOnlinePlayer(arg);
        if (pl == null) {
            sendTemplatedMessage(playerUUID, "hammer.player.notonline", true, true);
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

        pl.kickPlayer(reason);

        String plName = "Console";
        if (!isConsole) {
            plName = core.getActionProvider().getPlayerTranslator().uuidToPlayerName(playerUUID);
        }

        HammerText[] msg = createReasons(pl.getName(), plName, reason);
        if (!quietKick && core.getActionProvider().getConfigurationProvider().notifyServerOfBans()) {
            for (HammerText m : msg) {
                core.getActionProvider().getMessageSender().sendMessageToAllPlayers(m);
            }
        } else {
            for (HammerText m : msg) {
                core.getActionProvider().getMessageSender().sendMessageToPlayersWithPermission("hammer.notify", m);
            }
        }

        return true;
    }

    private HammerText[] createReasons(String playerKicked, String playerKicking, String reason) {
        HammerText[] t = new HammerText[2];
        t[0] = new HammerTextBuilder().addText(playerKicked, HammerTextColours.WHITE)
                .addText(" " + messageBundle.getString("hammer.kick.kickMessage"), HammerTextColours.RED)
                .addText(" " + playerKicking, HammerTextColours.WHITE).build();
        t[1] = new HammerTextBuilder().addText(MessageFormat.format(messageBundle.getString("hammer.kick.reason"), reason)
                , HammerTextColours.RED).build();
        return t;
    }
}
