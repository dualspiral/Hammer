package uk.co.drnaylor.minecraft.hammer.core.interfaces;

import java.util.UUID;

public abstract class PlayerPermissionCheckBase {

    /**
     * Returns whether the specified player has the specified permission
     *
     * @param player The {@link UUID} of the player.
     * @param permissionNode The permission node to check.
     * @return <code>true</code> if permission is granted.
     */
    public abstract boolean hasPermission(UUID player, String permissionNode);

    /**
     * Returns whether the specified player has permission to ban players
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    public final boolean hasPermissionToBan(UUID player) {
        return hasPermission(player, "hammer.ban.normal");
    }

    /**
     * Returns whether the specified player has permission to ban players permanently
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    public final boolean hasPermissionToBanPermanent(UUID player) {
        return hasPermission(player, "hammer.ban.normal");
    }


    /**
     * Returns whether the specified player has permission to ban players from all servers
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    public final boolean hasPermissionToBanOnAllServers(UUID player) {
        return hasPermission(player, "hammer.ban.normal");
    }


    /**
     * Returns whether the specified player has permission to ban players temporarily
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    public final boolean hasPermissionToBanTemporarily(UUID player) {
        return hasPermission(player, "hammer.ban.normal");
    }


    /**
     * Returns whether the specified player has permission to unban players
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    public final boolean hasPermissionToUnban(UUID player) {
        return hasPermission(player, "hammer.ban.normal");
    }


    /**
     * Returns whether the specified player has permission to unban players who
     * have received an all server ban.
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    public final boolean hasPermissionToUnbanFromAllServers(UUID player) {
        return hasPermission(player, "hammer.ban.normal");
    }


    /**
     * Returns whether the specified player has permission to unban players with
     * a permanent ban
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    public final boolean hasPermissionToUnbanPermanent(UUID player) {
        return hasPermission(player, "hammer.ban.normal");
    }


    /**
     * Returns whether the specified player has permission to ban IPs
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    public final boolean hasPermissionToBanIP(UUID player) {
        return hasPermission(player, "hammer.ban.normal");
    }


    /**
     * Returns whether the specified player has permission to unban IPs
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    public final boolean hasPermissionToUnbanIP(UUID player) {
        return hasPermission(player, "hammer.ban.normal");
    }


    /**
     * Returns whether the specified player is exempted from bans.
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    public final boolean hasExemptionFromBan(UUID player) {
        return hasPermission(player, "hammer.ban.normal");
    }
}
