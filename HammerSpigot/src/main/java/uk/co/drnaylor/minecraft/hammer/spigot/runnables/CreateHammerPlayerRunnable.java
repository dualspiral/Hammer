package uk.co.drnaylor.minecraft.hammer.spigot.runnables;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.spigot.HammerSpigot;
import uk.co.drnaylor.minecraft.hammer.spigot.coreimpl.BukkitHammerPlayerTranslator;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

public class CreateHammerPlayerRunnable implements Runnable {
    private final HammerSpigot plugin;
    private final Set<Player> player;

    public CreateHammerPlayerRunnable(HammerSpigot plugin) {
        this.player = new HashSet<>();
        this.plugin = plugin;
    }

    public void addPlayer(Player player) {
        this.player.add(player);
    }

    @Override
    public void run() {
        if (player.isEmpty()) {
            return;
        }

        try {
            HashSet<Player> pl = new HashSet<>();
            pl.addAll(player);
            player.clear();

            for (Player p : pl) {
                if (!p.isOnline()) {
                    pl.remove(p);
                }
            }

            // Process them on the async thread!
            try (DatabaseConnection conn = plugin.getHammerCore().getDatabaseConnection()) {
                conn.getPlayerHandler().updatePlayers(BukkitHammerPlayerTranslator.getHammerPlayers(pl.toArray(new Player[0])));
            }
        } catch (Exception ex) {

        }
    }
    
}
