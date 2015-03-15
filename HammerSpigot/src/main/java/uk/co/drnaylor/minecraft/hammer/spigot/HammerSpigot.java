package uk.co.drnaylor.minecraft.hammer.spigot;

import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;

import java.util.Collection;

public class HammerSpigot extends HammerBukkitPlugin {

    @Override
    protected Player[] getOnlinePlayers() {
        Collection<Player> pl = ImmutableList.copyOf(this.getServer().getOnlinePlayers());
        return pl.toArray(new Player[0]);
    }
}
