package uk.co.drnaylor.minecraft.hammer.bukkit;

import org.bukkit.entity.Player;

public class HammerBukkit extends HammerBukkitPlugin {

    @Override
    protected Player[] getOnlinePlayers() {
        return this.getServer().getOnlinePlayers();
    }
}
