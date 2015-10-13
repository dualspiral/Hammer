package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerPermissions;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.BanFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.UnbanFlagEnum;
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
public class UnbanCommandCore extends CommandCore {

    public UnbanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.unban.normal");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> entries = new ArrayList<>();
        entries.add(new ParserEntry("flags", new FlagParser<>(UnbanFlagEnum.class), true));
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

            List<HammerPlayerInfo> hpi = opt.get();
            String playerName = hpi.get(0).getName();

            // Get the player to unban if we can. We want the last known
            UUID unban = null;
            final WrappedPlayer player = core.getWrappedServer().getPlayer(hpi.get(0).getName());
            if (player != null) {
                unban = player.getUUID();
            }

            List<UUID> uuids = new ArrayList<>();

            // If we don't have them on this server...
            if (unban == null) {
                // ...do we have them in the Hammer DB?
                uuids.addAll(hpi.stream().map(HammerPlayerInfo::getUUID).collect(Collectors.toList()));
            } else {
                // Otherwise, we want them in the set.
                uuids.add(unban);
            }

            Optional<List<UnbanFlagEnum>> flags = arguments.<List<UnbanFlagEnum>>getArgument("flags");
            List<UnbanFlagEnum> flag;
            if (flags.isPresent()) {
                flag = flags.get();
            } else {
                flag = Collections.emptyList();
            }

            // If we have a ban...
            UUID bannee = null;
            int serverId =  core.getWrappedServer().getConfiguration().getConfigIntegerValue("server", "id");
            boolean ban = false;
            for (UUID u : uuids) {
                 HammerPlayerBan ban2 = conn.getBanHandler().getPlayerBanForServer(u, serverId);
                 if (ban2 == null) {
                     continue;
                 }

                 ban = true;
                 if (bannee != null && !bannee.equals(ban2.getBannedUUID())) {
                     // OK, if this is the case, then we need to tell the user of this problem...
                     sendTemplatedMessage(source, "hammer.unban.ambiguous", true, true, playerName);
                     return true;
                 } else {
                     if (ban2.isPermBan() && !flag.contains(UnbanFlagEnum.PERM)) {
                         sendTemplatedMessage(source, "hammer.unban.permanent", true, true);
                         return true;
                     }

                     if (ban2.getServerId() == null && !flag.contains(UnbanFlagEnum.ALL_SERVER)) {
                         sendTemplatedMessage(source, "hammer.unban.allservers", true, true);
                         return true;
                     }

                     bannee = ban2.getBannedUUID();
                 }
            }

            // You can't unban those who aren't banned!
            if (!ban) {
                sendTemplatedMessage(source, "hammer.unban.noban", true, true, playerName);
                return true;
            }

            // If you get here, then we have a ban to undo!
            conn.getBanHandler().unbanFromServer(bannee, serverId);
            if (player != null) {
                core.getWrappedServer().getScheduler().runSyncNow(player::unban);
                playerName = player.getName();
            }

            // Unban from all servers if that's needed.
            if (flag.contains(UnbanFlagEnum.ALL_SERVER)) {
                conn.getBanHandler().unbanFromAllServers(bannee);
            }

            // Unbanned. Tell the notified.
            sendUnbanMessage(playerName, source, flag.contains(UnbanFlagEnum.ALL_SERVER));
            return true;
        } catch (Exception ex) {
            throw new HammerException("Command failed to execute", ex);
        } 
    }

    @Override
    public HammerText getUsageMessage() {
        return new HammerTextBuilder().add("/unban [-a] [-p] player", HammerTextColours.YELLOW).build();
    }

    private void sendUnbanMessage(String playerName, WrappedCommandSource source, boolean allFlag) {
        HammerTextBuilder htb = new HammerTextBuilder();
        htb.add(playerName + " ", HammerTextColours.WHITE);
        if (allFlag) {
            htb.add(messageBundle.getString("hammer.unban.unbanAllServers"), HammerTextColours.GREEN);
        } else {
            htb.add(messageBundle.getString("hammer.unban.unbanOneServer"), HammerTextColours.GREEN);
        }

        htb.add(" " + source.getName(), HammerTextColours.WHITE);
        core.getWrappedServer().sendMessageToPermissionGroup(htb.build(), HammerPermissions.notify);
    }
}
