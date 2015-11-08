package uk.co.drnaylor.minecraft.hammer.core.commands;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.KickAllFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.KickFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.FlagParser;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.StringParser;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KickAllCommandCore extends CommandCore {

    public KickAllCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.kickall");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> entries = new ArrayList<>();
        entries.add(new ParserEntry("kickall", new FlagParser<>(KickAllFlagEnum.class), true));
        entries.add(new ParserEntry("reason", new StringParser(true), true));
        return entries;
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
    protected boolean executeCommand(WrappedCommandSource source, ArgumentMap arguments, DatabaseConnection conn) throws HammerException {
        Optional<List<KickAllFlagEnum>> flagOptional = arguments.<List<KickAllFlagEnum>>getArgument("kickall");
        if (flagOptional.isPresent()) {
            if (flagOptional.get().contains(KickAllFlagEnum.WHITELIST)) {
                if (source.hasPermission("hammer.whitelist")) {
                    core.getWrappedServer().setWhitelist(true);
                    sendTemplatedMessage(source, "hammer.kickall.whitelist", false, true);
                } else {
                    sendTemplatedMessage(source, "hammer.kickall.nowhitelist", true, true);
                    return true;
                }
            }
        }

        Optional<String> reasonOptional = arguments.<String>getArgument("reason");
        core.getWrappedServer().kickAllPlayers(source, reasonOptional.isPresent() ? reasonOptional.get() : "You have all been kicked from the server.");
        sendTemplatedMessage(source, "hammer.kickall", false, true);
        return true;
    }

    @Override
    protected String commandName() {
        return "kickall";
    }
}
