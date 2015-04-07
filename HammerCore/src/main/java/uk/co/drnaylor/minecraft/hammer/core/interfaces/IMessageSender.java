package uk.co.drnaylor.minecraft.hammer.core.interfaces;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

import java.util.Collection;
import java.util.UUID;

/**
 * Interface that defines how to send a message to a player or the console.
 */
public interface IMessageSender {

    /**
     * Sends a message to all players.
     *
     * @param messages The {@link Collection} of {@link HammerText}s to send.
     */
    void sendMessageToAllPlayer(Collection<HammerText> messages);

    /**
     * Sends a message to a player.
     *
     * @param uuid The {@link UUID} of the player to send a message to.
     * @param messages The {@link Collection} of {@link HammerText}s to send.
     */
    void sendMessageToPlayer(UUID uuid, Collection<HammerText> messages);

    /**
     * Sends a message to the players that are online with a specific permission node.
     *
     * @param permissionNode The permission node to use.
     * @param messages The messages to send.
     */
    void sendMessageToPlayersWithPermission(String permissionNode, Collection<HammerText> messages);

    /**
     * Sends a message to the console.
     *
     * @param message The {@link Collection} of {@link HammerText}s to send.
     */
    void sendMessageToConsole(Collection<HammerText> message);
}
