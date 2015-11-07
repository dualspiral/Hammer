package uk.co.drnaylor.minecraft.hammer.core.runnables;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BanCheckRunnable implements Runnable {

    private final HammerCore core;

    public BanCheckRunnable(HammerCore core) {
        this.core = core;
    }

    @Override
    public void run() {
        if (!core.getConfig().getConfig().getNode("pollBans").getBoolean()) {
            return;
        }

        // Get the server bans for any players currently online.
        Set<UUID> players = core.getWrappedServer().getOnlinePlayers().stream().map(WrappedPlayer::getUUID).collect(Collectors.toSet());

        List<HammerPlayerBan> bans;
        try (DatabaseConnection dg = core.getDatabaseConnection()){
            bans = dg.getBanHandler().getPlayerBansForServer(players);
        } catch (Exception e) {
            // Let's not worry too much
            core.getWrappedServer().getLogger().warn("There was an error running the ban check task. It will be retried later.");
            e.printStackTrace();
            return;
        }

        if (bans.isEmpty()) {
            // No bans here!
            return;
        }

        final WrappedServer s = core.getWrappedServer();
        bans.forEach(b -> {
            WrappedPlayer wp = s.getPlayer(b.getBannedUUID());
            if (wp != null) {
                String name = wp.getName();

                // Ban in sync!
                s.getScheduler().runSyncNow(() -> wp.ban(s.getConsole(), b.getReason()));
                s.sendMessageToPermissionGroup(new HammerTextBuilder().add("[Hammer] The player ", HammerTextColours.RED)
                        .add(name, HammerTextColours.WHITE).add(" has been kicked as they have been banned from elsewhere.").build(), "hammer.notify");
                s.sendMessageToPermissionGroup(new HammerTextBuilder().add("[Hammer] Reason: " + b.getReason(), HammerTextColours.RED).build(), "hammer.notify");

            }
        });
    }
}
