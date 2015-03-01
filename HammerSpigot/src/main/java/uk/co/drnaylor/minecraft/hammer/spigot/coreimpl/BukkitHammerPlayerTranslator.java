package uk.co.drnaylor.minecraft.hammer.spigot.coreimpl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayer;

public class BukkitHammerPlayerTranslator {
    private BukkitHammerPlayerTranslator() {}

    public static Collection<HammerPlayer> getHammerPlayers(Collection<Player> players) {
        return getHammerPlayers(players.toArray(new Player[0]));
    }

    public static Collection<HammerPlayer> getHammerPlayers(Player[] players) {
        ArrayList<HammerPlayer> pls = new ArrayList<>();
        for (Player pl : players) {
            pls.add(getHammerPlayer(pl));
        }

        return pls;
    }

    public static HammerPlayer getHammerPlayer(Player player) {
        InetSocketAddress addr = player.getAddress();
        String ip = addr != null ? addr.toString().substring(1).split(":")[0] : "127.0.0.1";
        return new HammerPlayer(player.getUniqueId(), player.getName(), ip);
    }
}
