package uk.co.drnaylor.minecraft.hammer.core.interfaces;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

import java.util.UUID;

/**
 * Interface that defines how to send a message to a player or the console.
 */
public interface IMessageSender {

    /**
     * Sends a message to all players.
     *
     * @param messages The {@link HammerText}s to send.
     */
    void sendMessageToAllPlayers(HammerText messages);

    /**
     * Sends a message to a player.
     *
     * @param uuid The {@link UUID} of the player to send a message to.
     * @param messages The {@link HammerText}s to send.
     */
    void sendMessageToPlayer(UUID uuid, HammerText messages);

    /**
     * Sends a message to the players that are online with a specific permission node.
     *
     * @param permissionNode The permission node to use.
     * @param messages The messages to send.
     */
    void sendMessageToPlayersWithPermission(String permissionNode, HammerText messages);

    /**
     * Sends a message to the console.
     *
     * @param message The {@link HammerText}s to send.
     */
    void sendMessageToConsole(HammerText message);
}
