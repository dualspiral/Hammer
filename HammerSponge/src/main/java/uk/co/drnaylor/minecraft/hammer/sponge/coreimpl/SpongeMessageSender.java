package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IMessageSender;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextFormats;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextToTextColorCoverter;

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
        game.getServer().broadcastMessage(constructMessage(messages));
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
                pl.sendMessage(ChatTypes.SYSTEM, constructMessage(messages));
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
        Text messageToSend = constructMessage(messages);

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
        game.getServer().getConsole().sendMessage(constructMessage(message));
    }

    /**
     * Constructs a Sponge {@link Text} message from the provided {@link HammerText} objects.
     *
     * @param text The input {@link HammerText} objects
     * @return The constructed {@link Text} object.
     */
    private Text constructMessage(HammerText text) {
        TextBuilder tb = Texts.builder();

        for (HammerText.Element t : text.getElements()) {
            TextColor tc = HammerTextToTextColorCoverter.getCodeFromHammerText(t.colour);
            TextStyle s = TextStyles.of();
            for (HammerTextFormats f : t.formats) {
                TextStyle style = HammerTextToTextColorCoverter.getCodeFromHammerText(f);
                if (style != null) {
                    s.and(style);
                }
            }

            tb.append(Texts.of(tc, s, t.message));
        }

        return tb.build();
    }
}
