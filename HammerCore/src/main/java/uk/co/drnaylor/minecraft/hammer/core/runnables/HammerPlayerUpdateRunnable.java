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
    private final Set<WrappedPlayer> player;
    private final HammerCore core;
    private final HashSet<WrappedPlayer> pl = new HashSet<>();

    public HammerPlayerUpdateRunnable(HammerCore core) {
        this.core = core;
        this.player = new HashSet<>();
    }

    public void addPlayer(WrappedPlayer pl) {
        player.add(pl);
    }

    @Override
    public void run() {
        if (player.isEmpty()) {
            return;
        }

        try {
            pl.addAll(player);
            player.clear();

            List<HammerPlayerInfo> players = new ArrayList<>();
            for (WrappedPlayer p : pl) {
                HammerPlayerInfo hp = p.getHammerPlayer();
                if (hp != null) {
                    players.add(hp);
                }
            }

            if (players.isEmpty()) {
                return;
            }

            // Process them on the async thread!
            try (DatabaseConnection conn = core.getDatabaseConnection()) {
                conn.getPlayerHandler().updatePlayers(players);
                pl.clear();
            }
        } catch (Exception ex) {
            Logger.getAnonymousLogger().warning("Could not update Hammer with latest player information. Will try again during the next cycle.");
        }
    }
}
