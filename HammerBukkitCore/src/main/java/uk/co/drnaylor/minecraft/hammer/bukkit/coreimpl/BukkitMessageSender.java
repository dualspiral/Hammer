package uk.co.drnaylor.minecraft.hammer.bukkit.coreimpl;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
        Bukkit.broadcastMessage(constructMessage(messages));
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
            player.sendMessage(constructMessage(messages));
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
        Bukkit.broadcast(constructMessage(messages), permissionNode);
    }

    /**
     * Sends a message to the console.
     *
     * @param message The {@link Collection} of {@link HammerText}s to send.
     */
    @Override
    public void sendMessageToConsole(HammerText message) {
        Bukkit.getConsoleSender().sendMessage(constructMessage(message));
    }

    /**
     * Constructs a message from the collection of {@link HammerText} messages.
     *
     * @param message The {@link HammerText} messages.
     * @return The completed message.
     */
    private String constructMessage(HammerText message) {
        StringBuilder sb = new StringBuilder();

        for (HammerText.Element t : message.getElements()) {
            if (sb.length() > 0) {
                sb.append(ChatColor.RESET);
            }

            convertColour(t.colour, sb);
            convertFormats(t.formats, sb);

            sb.append(t.message);
        }

        return sb.toString();
    }

    private void convertColour(HammerTextColours colour, StringBuilder sb) {
        ChatColor c = HammerTextToCodeConverter.getCodeFromHammerText(colour);
        if (c != null) {
            sb.append(c);
        }
    }

    private void convertFormats(HammerTextFormats[] formats, StringBuilder sb) {
        for (HammerTextFormats f : formats) {
            sb.append(HammerTextToCodeConverter.getCodeFromHammerText(f));
        }
    }
}
