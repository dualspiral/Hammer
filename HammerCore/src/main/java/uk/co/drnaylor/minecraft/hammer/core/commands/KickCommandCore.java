package uk.co.drnaylor.minecraft.hammer.core.commands;

import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerPermissions;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.BanFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.KickFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.*;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.text.MessageFormat;
import java.util.*;

public class KickCommandCore extends CommandCore {

    public KickCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.kick");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> entries = new ArrayList<>();
        entries.add(new ParserEntry("flags", new FlagParser<>(KickFlagEnum.class), true));
        entries.add(new ParserEntry("player", new OnlinePlayerParser(core), false));
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
        if (arguments.isEmpty()) {
            sendUsageMessage(source);
            return true;
        }

        Optional<List<KickFlagEnum>> flags = arguments.<List<KickFlagEnum>>getArgument("flags");
        List<KickFlagEnum> flag;
        if (flags.isPresent()) {
            flag = flags.get();
        } else {
            flag = Collections.emptyList();
        }

        WrappedPlayer pl = arguments.<WrappedPlayer>getArgument("player").get();

        Optional<String> reasonOptional = arguments.<String>getArgument("reason");
        String reason = reasonOptional.isPresent() ? reasonOptional.get() : "You have been kicked!";
        pl.kick(reason);

        // Get the name of the person doing the kicking.
        String plName = source.getName();

        HammerText[] msg = createReasons(pl.getName(), plName, reason);
        if (flag.contains(KickFlagEnum.NOISY) || (!flag.contains(KickFlagEnum.QUIET) && core.getConfig().getConfig().getNode("notifyAllOnBan").getBoolean())) {
            for (HammerText m : msg) {
                core.getWrappedServer().sendMessageToServer(m);
            }
        } else {
                core.getWrappedServer().sendMessageToPermissionGroup(
                    new HammerTextBuilder().add("[Hammer] This kick is quiet. Only those with notify permissions will see this.", HammerTextColours.RED).build(),
                    HammerPermissions.notify);
            for (HammerText m : msg) {
                core.getWrappedServer().sendMessageToPermissionGroup(m, HammerPermissions.notify);
            }
        }

        return true;
    }

    @Override
    protected String commandName() {
        return "kick";
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
