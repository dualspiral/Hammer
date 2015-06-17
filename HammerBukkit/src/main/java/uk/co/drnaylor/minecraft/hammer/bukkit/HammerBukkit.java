package uk.co.drnaylor.minecraft.hammer.bukkit;

import org.bukkit.entity.Player;

public class HammerBukkit extends HammerBukkitPlugin {

    @Override
    public Player[] getOnlinePlayers() {
        return this.getServer().getOnlinePlayers();
    }
}
