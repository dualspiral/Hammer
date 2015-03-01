package uk.co.drnaylor.minecraft.hammer.core.interfaces;

import java.util.UUID;

public interface IPlayerPermissionCheck {

    /**
     * Returns whether the specified player has permission to ban players
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    boolean hasPermissionToBan(UUID player);

    /**
     * Returns whether the specified player has permission to ban players permanently
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    boolean hasPermissionToBanPermanent(UUID player);

    /**
     * Returns whether the specified player has permission to ban players from all servers
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    boolean hasPermissionToBanOnAllServers(UUID player);

    /**
     * Returns whether the specified player has permission to ban players temporarily
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    boolean hasPermissionToBanTemporarily(UUID player);

    /**
     * Returns whether the specified player has permission to unban players
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    boolean hasPermissionToUnban(UUID player);

    /**
     * Returns whether the specified player has permission to unban players who
     * have received an all server ban.
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    boolean hasPermissionToUnbanFromAllServers(UUID player);

    /**
     * Returns whether the specified player has permission to unban players with
     * a permanent ban
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    boolean hasPermissionToUnbanPermanent(UUID player);

    /**
     * Returns whether the specified player has permission to ban IPs
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    boolean hasPermissionToBanIP(UUID player);

    /**
     * Returns whether the specified player has permission to unban IPs
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    boolean hasPermissionToUnbanIP(UUID player);

    /**
     * Returns whether the specified player is exempted from bans.
     * 
     * @param player The UUID of the player to check the permission of
     * @return <code>true</code> if permission is granted
     */
    boolean hasExemptionFromBan(UUID player);
}
