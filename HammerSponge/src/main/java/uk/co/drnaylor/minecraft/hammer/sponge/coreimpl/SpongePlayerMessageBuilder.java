package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.ShiftClickAction;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.message.Message;
import org.spongepowered.api.text.message.MessageBuilder;
import org.spongepowered.api.text.message.Messages;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerMessageBuilder;

import java.util.List;
import java.util.UUID;

public class SpongePlayerMessageBuilder implements IPlayerMessageBuilder {

    // Injected members

    @Inject
    private Game game;

    @Inject
    private Logger logger;

    private Server server = null;

    /**
     * Gets the {@link org.spongepowered.api.Server} instance from the {@link org.spongepowered.api.Game}
     * instance. Does lazy loading.
     *
     * @return The server.
     */
    private Server getServer() {
        if (server == null) {
            Optional<Server> serverOptional = game.getServer();
            if (serverOptional.isPresent()) {
                server = serverOptional.get();
            }
        }

        return server;
    }

    private void sendMessageToPlayer(UUID uuid, Message... messages) {
        Optional<Player> player = getServer().getPlayer(uuid);
        if (player.isPresent()) {
            player.get().sendMessage(messages);
        }
    }

    /**
     * Returns whether the console UUID is used here.
     *
     * @param uuid The {@link java.util.UUID} to check.
     * @return <code>true</code> if this is the console.
     */
    private boolean isConsole(UUID uuid) {
        return HammerConstants.consoleUUID.equals(uuid);
    }

    @Override
    public void sendNoPermsMessage(UUID uuid) {
        // Create the message.
        Message m = Messages.builder("[Hammer] You do not have permission to perform that action.")
                .color(TextColors.RED).build();

        // Send it
        sendMessageToPlayer(uuid, m);
    }

    @Override
    public void sendNoPlayerMessage(UUID uuid) {

    }

    @Override
    public void sendAlreadyBannedMessage(UUID uuid) {

    }

    @Override
    public void sendUsageMessage(UUID uuid, String message) {

    }

    @Override
    public void sendErrorMessage(UUID uuid, String message) {

    }

    @Override
    public void sendStandardMessage(UUID player, String message, boolean withTag) {

    }

    @Override
    public void sendAlreadyBannedFailMessage(UUID uuidToBan) {

    }

    @Override
    public void sendToPermMessage(UUID playerUUID) {

    }

    @Override
    public void sendToAllMessage(UUID playerUUID) {

    }
}
