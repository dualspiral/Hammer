package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;

public class TempBanCommandCore extends BaseBanCommandCore {

    private final Pattern timeFormat = Pattern.compile("^(\\d+)([dhm])$");

    public TempBanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.ban.temp");
    }

    @Override
    protected int minArguments() {
        return 2;
    }

    @Override
    protected boolean performSpecificActions(HammerCreatePlayerBanBuilder builder, Iterator<String> argumentIterator) {
        // One argument, temp ban
        String arg = argumentIterator.next();
        Matcher m = timeFormat.matcher(arg);
        if (!m.matches()) {
            return false;
        }

        // Get the last character.
        Integer number = Integer.parseInt(m.group(1));
        String unit = m.group(2);
        Date until = new Date();

        int u;
        if (unit.equalsIgnoreCase("d")) {
            u = Calendar.DATE;
        } else if (unit.equalsIgnoreCase("h")) {
            u = Calendar.HOUR;
        } else if (unit.equalsIgnoreCase("m")) {
            // It has to be minutes
            u = Calendar.MINUTE;
        } else {
            return false;
        }

        until = add(until, u, number);
        builder.setTemporary(until);
        return true;
    }

    @Override
    public HammerText getUsageMessage() {
        return new HammerTextBuilder().add("/tempban [-a -q] name (time)(d|h|m) reason", HammerTextColours.YELLOW).build();
    }

    private Date add(Date date, int unit, int span)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(unit, span);
        return cal.getTime();
    }

    @Override
    protected String createReason(Iterator<String> argumentIterator, List<String> reasons) {
        if (!argumentIterator.hasNext()) {
            argumentIterator = Arrays.asList("Temporarily Banned.".split(" ")).iterator();
        }

        return super.createReason(argumentIterator, null);
    }

    /**
     * Checks other bans, to see if any are in force.
     * @param bannedPlayer
     * @param conn
     * @param isGlobal
     * @return
     * @throws HammerException 
     */
    @Override
    protected BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, boolean isGlobal) throws HammerException {
        if (conn.getBanHandler().getPlayerBanForServer(bannedPlayer, core.getWrappedServer().getConfiguration().getConfigIntegerValue("server", "id")) != null) {
            return new BanInfo(BanStatus.NO_ACTION, null);
        }

        return new BanInfo(BanStatus.CONTINUE, null);
    }
}
