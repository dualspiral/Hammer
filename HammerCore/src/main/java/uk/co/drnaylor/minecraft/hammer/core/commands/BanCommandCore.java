package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.BanFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.*;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;

/**
 * Provides the core Ban Command, based on the player and arguments sent down.
 */
public class BanCommandCore extends BaseBanCommandCore {

    public BanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.ban.normal");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> entries = new ArrayList<>();
        entries.add(new ParserEntry("flags", new FlagParser<>(BanFlagEnum.class), true));
        entries.add(new ParserEntry("player", new HammerPlayerParser(core), false));
        entries.add(new ParserEntry("reason", new StringParser(true), false));
        return entries;
    }

    @Override
    protected boolean performSpecificActions(HammerCreatePlayerBanBuilder builder, ArgumentMap argumentMap) {
        // Nothing
        return true;
    }

    @Override
    public HammerText getUsageMessage() {
        return new HammerTextBuilder().add("/ban [-a -q] name reason", HammerTextColours.YELLOW).build();
    }

    @Override
    protected BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, boolean isGlobal) throws HammerException {
        // Check if they are already banned.
        List<HammerPlayerBan> bans = conn.getBanHandler().getPlayerBans(bannedPlayer);
        if (isGlobal) {
            List<String> reasons = new ArrayList<>();
            boolean isPerm = false;
            for (HammerPlayerBan ban : bans) {
                if (!ban.isTempBan()) {
                    reasons.add(ban.getReason());
                }

                if (ban.getServerId() == null) {
                    return new BanInfo(BanStatus.NO_ACTION, null);
                }

                if (ban.isPermBan()) {
                    isPerm = true;
                }
            }

            // If it's going global, then unban all current.
            conn.getBanHandler().unbanFromAllServers(bannedPlayer);
            return new BanInfo(isPerm ? BanStatus.TO_PERM : BanStatus.CONTINUE, reasons);
        }

        for (HammerPlayerBan ban : bans) {
            Integer serverId = ban.getServerId();
            if (serverId == null || Objects.equals(serverId, core.getWrappedServer().getConfiguration().getConfigIntegerValue("server", "id"))) {
                // Banned. No further action.
                return new BanInfo(BanStatus.NO_ACTION, null);
            }
        }

        return new BanInfo(BanStatus.CONTINUE, null);
    }
}
