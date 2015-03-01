package uk.co.drnaylor.minecraft.hammer.spigot.coreimpl;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerPermissionCheck;

public class BukkitPlayerPermissionCheck implements IPlayerPermissionCheck {

    @Override
    public boolean hasPermissionToBan(UUID player) {
        return checkPermission(player, "hammer.ban.normal");
    }

    @Override
    public boolean hasPermissionToBanPermanent(UUID player) {
        return checkPermission(player, "hammer.ban.perm");
    }

    @Override
    public boolean hasPermissionToBanOnAllServers(UUID player) {
        return checkPermission(player, "hammer.ban.all");
    }

    @Override
    public boolean hasPermissionToBanTemporarily(UUID player) {
        return checkPermission(player, "hammer.ban.temp");
    }

    @Override
    public boolean hasPermissionToUnban(UUID player) {
        return checkPermission(player, "hammer.unban.norm");
    }

    @Override
    public boolean hasPermissionToUnbanFromAllServers(UUID player) {
        return checkPermission(player, "hammer.unban.all");
    }

    @Override
    public boolean hasPermissionToUnbanPermanent(UUID player) {
        return checkPermission(player, "hammer.unban.perm");
    }

    @Override
    public boolean hasPermissionToBanIP(UUID player) {
        return checkPermission(player, "hammer.ban.ip");
    }

    @Override
    public boolean hasPermissionToUnbanIP(UUID player) {
        return checkPermission(player, "hammer.unban.ip");
    }

    @Override
    public boolean hasExemptionFromBan(UUID player) {
        return checkPermission(player, "hammer.ban.exempt");
    }

    /**
     * Checks if the specified user has the specified permission.
     * 
     * @param player The player.
     * @param permissionNode The permission node.
     * @return <code>true</code> if the player exists and has permission.
     */
    private boolean checkPermission(UUID player, String permissionNode) {
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
