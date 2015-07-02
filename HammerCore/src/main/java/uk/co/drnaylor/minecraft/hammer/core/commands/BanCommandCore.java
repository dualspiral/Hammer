package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
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

    private final Pattern timeFormat = Pattern.compile("^(\\d+)([dhm])$");

    public BanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.ban.normal");
    }

    private Date timeParser(String time) {
        Matcher m = timeFormat.matcher(time);
        if (m.matches()) {
            // Get the last character.
            Integer number = Integer.parseInt(m.group(1));
            String unit = m.group(2);
            Date until = new Date();

            int u;
            if (unit.equalsIgnoreCase("d")) {
                u = Calendar.DATE;
            } else if (unit.equalsIgnoreCase("h")) {
                u = Calendar.HOUR;
            } else {
                // It has to be minutes
                u = Calendar.MINUTE;
            }

            return add(until, u, number);
        }

        return null;
    }

    private static Date add(Date date, int unit, int span)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(unit, span);
        return cal.getTime();
    }

    @Override
    protected int minArguments() {
        return 2;
    }

    @Override
    protected boolean performSpecificActions(HammerCreatePlayerBanBuilder builder, Iterator<String> argumentIterator) {
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
