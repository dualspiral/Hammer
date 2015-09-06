package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import org.spongepowered.api.entity.living.player.Player;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayer;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;

public class SpongeHammerPlayerTranslator  {

    private SpongeHammerPlayerTranslator() {}

    public static Collection<HammerPlayer> getHammerPlayers(Player[] players) {
        ArrayList<HammerPlayer> pls = new ArrayList<>();
        for (Player pl : players) {
            pls.add(getHammerPlayer(pl));
        }

        return pls;
    }

    private static HammerPlayer getHammerPlayer(Player player) {
        InetSocketAddress addr = player.getConnection().getAddress();
        String ip = addr.toString().substring(1).split(":")[0];
        return new HammerPlayer(player.getUniqueId(), player.getName(), ip);
    }
}
