package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

public class PermBanCommandCore extends BaseBanCommandCore {

    public PermBanCommandCore(HammerCore core) {
        super(core);
    }

    @Override
    protected int minArguments() {
        return 2;
    }

    @Override
    protected boolean performSpecificActions(HammerCreatePlayerBanBuilder builder, Iterator<String> argumentIterator) {
        builder.setPerm(true);
        return true;
    }

    @Override
    protected String getUsage() {
        return "/permban [-a -q] name reason";
    }

    @Override
    protected BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, boolean isGlobal) throws HammerException {
        // Check if they are already banned.
        List<HammerPlayerBan> bans = conn.getBanHandler().getPlayerBans(bannedPlayer);
        if (isGlobal) {
            List<String> reasons = new ArrayList<>();
            for (HammerPlayerBan ban : bans) {
                if (!ban.isTempBan()) {
                    reasons.add(ban.getReason());
                }

                if (ban.getServerId() == null && ban.isPermBan()) {
                    return new BanInfo(BanStatus.NO_ACTION, null);
                }
            }

            // If it's going global, then unban all current.
            conn.getBanHandler().unbanFromAllServers(bannedPlayer);
            return new BanInfo(BanStatus.CONTINUE, reasons);
        }

        for (HammerPlayerBan ban : bans) {
            Integer serverId = ban.getServerId();
            if (serverId == null) {
                if (ban.isPermBan()) {
                    // Global perm. No action.
                    return new BanInfo(BanStatus.NO_ACTION, null);
                }

                List<String> reason = new ArrayList<>();
                reason.add(ban.getReason());
                return new BanInfo(BanStatus.TO_GLOBAL, reason);
            } else if (serverId == core.getActionProvider().getConfigurationProvider().getServerId() && ban.isPermBan()) {
                return new BanInfo(BanStatus.NO_ACTION, null);
            }
        }

        conn.getBanHandler().unbanFromServer(bannedPlayer, core.getActionProvider().getConfigurationProvider().getServerId());
        return new BanInfo(BanStatus.CONTINUE, null);
    }
}
