package uk.co.drnaylor.minecraft.hammer.bukkit;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;

import java.util.Collection;

public class HammerBukkit extends HammerBukkitPlugin {

    @Override
    public Player[] getOnlinePlayers() {
        Collection<Player> pl = ImmutableList.copyOf(this.getServer().getOnlinePlayers());
        return pl.toArray(new Player[0]);
    }
}
