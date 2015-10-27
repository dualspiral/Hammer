package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.BanFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.*;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;

public class TempBanCommandCore extends BaseBanCommandCore {

    public TempBanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.ban.temp");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> entries = new ArrayList<>();
        entries.add(new ParserEntry("flags", new FlagParser<>(BanFlagEnum.class), true));
        entries.add(new ParserEntry("player", new HammerPlayerParser(core), false));
        entries.add(new ParserEntry("time", new TimespanParser(), false));
        entries.add(new ParserEntry("reason", new StringParser(true), true));
        return entries;
    }

    @Override
    protected String commandName() {
        return "tempban";
    }

    @Override
    protected boolean performSpecificActions(HammerCreatePlayerBanBuilder builder, ArgumentMap argumentMap) {
        Date until = new Date();
        until = add(until, Calendar.SECOND, argumentMap.<Integer>getArgument("time").get());
        builder.setTemporary(until);
        return true;
    }

    private Date add(Date date, int unit, int span)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(unit, span);
        return cal.getTime();
    }

    @Override
    protected String createReason(ArgumentMap argumentMap, List<String> reasons) {
        Optional<String> reason = argumentMap.getArgument("reason");
        if (!reason.isPresent()) {
            argumentMap.put("reason", "Temporarily Banned.");
        }

        return argumentMap.<String>getArgument("reason").get();
    }

    @Override
    protected BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, boolean isGlobal) throws HammerException {
        if (conn.getBanHandler().getPlayerBanForServer(bannedPlayer, core.getConfig().getConfig().getNode("server", "id").getInt()) != null) {
            return new BanInfo(BanStatus.NO_ACTION, null);
        }

        return new BanInfo(BanStatus.CONTINUE, null);
    }
}
