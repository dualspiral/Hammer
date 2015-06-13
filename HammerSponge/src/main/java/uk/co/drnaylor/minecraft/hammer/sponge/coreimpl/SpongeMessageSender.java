package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IMessageSender;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

import java.util.Collection;
import java.util.UUID;

public class SpongeMessageSender implements IMessageSender {

    @Inject
    private Game game;

    /**
     * Sends a message to all players.
     *
     * @param messages The {@link Collection} of {@link HammerText}s to send.
     */
    @Override
    public void sendMessageToAllPlayers(HammerText messages) {
        game.getServer().getBroadcastSink().sendMessage(HammerTextConverter.constructMessage(messages));
    }

    /**
     * Sends a message to a player.
     *
     * @param uuid     The {@link UUID} of the player to send a message to.
     * @param messages The {@link Collection} of {@link HammerText}s to send.
     */
    @Override
    public void sendMessageToPlayer(UUID uuid, HammerText messages) {
        Optional<Player> opt = game.getServer().getPlayer(uuid);
        if (opt.isPresent()) {
            Player pl = opt.get();
            if (pl.isOnline()) {
                pl.sendMessage(ChatTypes.SYSTEM, HammerTextConverter.constructMessage(messages));
            }
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
        Collection<Player> players = game.getServer().getOnlinePlayers();
        Text messageToSend = HammerTextConverter.constructMessage(messages);

        // I would sincerely love to use Java 8 streams here. Maybe I will eventually - Java 7 goes out of support soon,
        // right?
        //
        // Maybe I should start a campaign to get all server owners to update. Either that, or ask Mojang to support
        // Java 8 only. I can dream, right?
        for (Player p : players) {
            if (p.hasPermission(permissionNode)) {
                p.sendMessage(ChatTypes.SYSTEM, messageToSend);
            }
        }
    }

    /**
     * Sends a message to the console.
     *
     * @param message The {@link Collection} of {@link HammerText}s to send.
     */
    @Override
    public void sendMessageToConsole(HammerText message) {
        game.getServer().getConsole().sendMessage(HammerTextConverter.constructMessage(message));
    }
}
