package uk.co.drnaylor.minecraft.hammer.bukkit.coreimpl;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.PlayerPermissionCheckBase;

public class BukkitPlayerPermissionCheck extends PlayerPermissionCheckBase {

    @Override
    public boolean hasPermission(UUID player, String permissionNode) {
        if (player.equals(HammerConstants.consoleUUID)) {
            return true; // Console.
        }

        Player pl = Bukkit.getPlayer(player);

        // No player, no permission!
        if (pl == null) {
            return false;
        }

        return pl.hasPermission(permissionNode);
    }
}
