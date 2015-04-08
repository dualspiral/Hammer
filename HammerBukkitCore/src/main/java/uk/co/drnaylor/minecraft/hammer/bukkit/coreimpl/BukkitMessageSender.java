package uk.co.drnaylor.minecraft.hammer.bukkit.coreimpl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.bukkit.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.bukkit.text.HammerTextToCodeConverter;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IMessageSender;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextFormats;

import java.util.Collection;
import java.util.UUID;

/**
 * Sends messages to the player/console using Bukkit methods.
 */
public class BukkitMessageSender implements IMessageSender {

    /**
     * Sends a message to all players.
     *
     * @param messages The {@link HammerText}s to send.
     */
    @Override
    public void sendMessageToAllPlayers(HammerText messages) {
        Bukkit.broadcastMessage(HammerTextConverter.constructMessage(messages));
    }

    /**
     * Sends a message to a player.
     *
     * @param uuid     The {@link UUID} of the player to send a message to.
     * @param messages The {@link HammerText}s to send.
     */
    @Override
    public void sendMessageToPlayer(UUID uuid, HammerText messages) {
        Player player = Bukkit.getServer().getPlayer(uuid);
        if (player != null && player.isOnline()) {
            player.sendMessage(HammerTextConverter.constructMessage(messages));
        }
    }

    /**
     * Sends a message to the players that are online with a specific permission node.
     *
     * @param permissionNode The permission node to use.
     * @param messages       The messages to send.
     */
    @Override
    public void sendMessageToPlayersWithPermission(String permissionNode, HammerText messages) {
        Bukkit.broadcast(HammerTextConverter.constructMessage(messages), permissionNode);
    }

    /**
     * Sends a message to the console.
     *
     * @param message The {@link Collection} of {@link HammerText}s to send.
     */
    @Override
    public void sendMessageToConsole(HammerText message) {
        Bukkit.getConsoleSender().sendMessage(HammerTextConverter.constructMessage(message));
    }

}
