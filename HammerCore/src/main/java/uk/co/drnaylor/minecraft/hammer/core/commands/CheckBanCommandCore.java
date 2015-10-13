package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.BanFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.FlagParser;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.HammerPlayerParser;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.StringParser;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

@RunAsync
public class CheckBanCommandCore extends CommandCore {

    public CheckBanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.checkban");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> entries = new ArrayList<>();
        entries.add(new ParserEntry("player", new HammerPlayerParser(core), false));
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
        try {
            // Get the player out.
            Optional<List<HammerPlayerInfo>> opt = arguments.<List<HammerPlayerInfo>>getArgument("player");
            if (!opt.isPresent()) {
                // Something went wrong.
                throw new HammerException("No player argument");
            }

            String playerName = opt.get().get(0).getName();
            List<UUID> uuids = opt.get().stream().map(HammerPlayerInfo::getUUID).collect(Collectors.toList());
            if (uuids.size() > 1) {
                sendTemplatedMessage(source, "hammer.player.multiple", false, true, playerName);
                sendMessage(source, "------------------", false, false);
            }

            for (UUID uuid : uuids) {
                List<HammerPlayerBan> bans = conn.getBanHandler().getPlayerBans(uuid);
                if (uuids.size() > 1) {
                    sendMessage(source, "UUID: " + uuid.toString(), false, true);
                }

                if (bans.isEmpty()) {
                    sendTemplatedMessage(source, "hammer.player.check.nobans", false, true, playerName);
                } else {
                    sendTemplatedMessage(source, "hammer.player.check.bans", false, true, playerName, String.valueOf(bans.size()));
                    for (HammerPlayerBan b : bans) {
                        sendBanReason(b, source);
                    }
                }
            }

            return true;
        } catch (Exception ex) {
            throw new HammerException("An error occurred", ex);
        }
    }

    @Override
    public HammerText getUsageMessage() {
        return new HammerTextBuilder().add("/checkban <name>", HammerTextColours.YELLOW).build();
    }

    private void sendBanReason(HammerPlayerBan ban, WrappedCommandSource source) {
        sendMessage(source, "------------------", false, false);

        String server = ban.getServerId() == null ? messageBundle.getString("hammer.player.check.allservers") :
                MessageFormat.format(messageBundle.getString("hammer.player.check.serverid"), ban.getServerName(), ban.getServerId().toString());

        String modifier = "";
        if (ban.isPermBan()) {
            modifier = String.format(" %s ", messageBundle.getString("hammer.player.check.perm"));
        } else if (ban.isTempBan()) {
            modifier = String.format(" %s ", MessageFormat.format(messageBundle.getString("hammer.player.check.temp"),
                    core.createTimeStringFromOffset(ban.getDateOfUnban().getTime() - (new Date()).getTime())));
        }

        sendTemplatedMessage(source, "hammer.player.check.from", false, false, server, modifier);
        sendTemplatedMessage(source, "hammer.player.check.banned", false, false, dateFormatter.format(ban.getDateOfBan()));
        sendTemplatedMessage(source, "hammer.player.check.bannedby", false, false, ban.getBanningStaffName());
        sendTemplatedMessage(source, "hammer.player.check.reason", false, false, ban.getReason());
    }
}
