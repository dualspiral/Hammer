package uk.co.drnaylor.minecraft.hammer.core.runnables;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class HammerPlayerUpdateRunnable implements Runnable {
    private final Set<HammerPlayerInfo> player;
    private final HammerCore core;

    public HammerPlayerUpdateRunnable(HammerCore core) {
        this.core = core;
        this.player = new HashSet<>();
    }

    public void addPlayer(WrappedPlayer pl) {
        HammerPlayerInfo hp = pl.getHammerPlayer();

        if (hp != null) {
            player.add(pl.getHammerPlayer());
        }
    }

    @Override
    public void run() {
        if (player.isEmpty()) {
            return;
        }

        // In case a new player comes in, we transfer the players to a second set.
        // This way, new players who weren't sent to the DB won't get wiped.
        HashSet<HammerPlayerInfo> hpi = new HashSet<>();
        hpi.addAll(player);
        player.clear();

        // Process them on the async thread!
        try (DatabaseConnection conn = core.getDatabaseConnection()) {
            conn.getPlayerHandler().updatePlayers(hpi);
        } catch (Exception ex) {
            Logger.getAnonymousLogger().warning("Could not update Hammer with latest player information. Will try again during the next cycle.");

            // Put them back, we'll try again.
            player.addAll(hpi);
        }
    }
}
