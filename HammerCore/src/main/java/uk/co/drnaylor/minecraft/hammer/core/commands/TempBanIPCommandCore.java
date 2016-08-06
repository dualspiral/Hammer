package uk.co.drnaylor.minecraft.hammer.core.commands;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.StringParser;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.TimespanParser;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreateBanBuilder;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunAsync
public class TempBanIPCommandCore extends BanIPCommandCore {
    public TempBanIPCommandCore(HammerCore core) {
        super(core);
        permissionNodes.clear();
        permissionNodes.add("hammer.ipban.temp");
    }

    public List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> pe = super.createArgumentParserList();

        // Remove reason.
        pe.remove(pe.size() - 1);

        // Add time.
        pe.add(new ParserEntry("time", new TimespanParser(), false));
        pe.add(new ParserEntry("reason", new StringParser(true), true));
        return pe;
    }

    @Override
    public String commandName() {
        return "tempbanip";
    }

    @Override
    protected void performTempbanActions(ArgumentMap argumentMap, HammerCreateBanBuilder builder) {
        Date until = new Date();
        until = add(until, Calendar.SECOND, argumentMap.<Integer>getArgument("time").get());
        builder.setTemporary(until).setAll(false);

        if (!argumentMap.containsKey("reason")) {
            argumentMap.put("reason", "Temporarily Banned");
        }
    }

    private Date add(Date date, int unit, int span) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(unit, span);
        return cal.getTime();
    }
}
