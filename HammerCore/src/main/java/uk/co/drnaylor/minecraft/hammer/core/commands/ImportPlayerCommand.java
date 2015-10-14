package uk.co.drnaylor.minecraft.hammer.core.commands;

import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerPermissions;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.StringParser;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.runnables.MojangNameRunnable;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the common logic for the /importplayer command
 */
@RunAsync
public class ImportPlayerCommand extends CommandCore {

    public ImportPlayerCommand(HammerCore core) {
        super(core);
        permissionNodes = new ArrayList<>();
        permissionNodes.add(HammerPermissions.importPlayer);
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> entries = new ArrayList<>();
        entries.add(new ParserEntry("player", new StringParser(false), false));
        return entries;
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
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
    protected boolean executeCommand(WrappedCommandSource source, ArgumentMap arguments, DatabaseConnection conn) throws HammerException {
        if (arguments.isEmpty()) {
            source.sendMessage(getUsageMessage());
            return true;
        }

        String name = arguments.<String>getArgument("player").get();

        // Fire the web service off.
        MojangNameRunnable mnr = new MojangNameRunnable(source, core, name);
        Thread th = new Thread(mnr);

        // We set this to be a Daemon thread.
        th.setDaemon(true);
        th.start();

        HammerTextBuilder htb = new HammerTextBuilder().add(HammerConstants.textTag + " Contacting Mojang for information about ", HammerTextColours.GREEN).add(name, HammerTextColours.YELLOW);
        source.sendMessage(htb.build());

        // This command is done at this point - the thread will do the work from here.
        return true;
    }

    @Override
    protected String commandName() {
        return "importplayer";
    }
}
