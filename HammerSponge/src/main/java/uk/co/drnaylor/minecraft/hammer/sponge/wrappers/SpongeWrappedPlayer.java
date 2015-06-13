package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

import java.util.UUID;

public class SpongeWrappedPlayer implements WrappedPlayer {

    private final Player player;

    public SpongeWrappedPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the name of the player.
     *
     * @return The name.
     */
    @Override
    public String getName() {
        return player.getName();
    }

    /**
     * Gets the Unique Identifier of the player.
     *
     * @return The {@link UUID}
     */
    @Override
    public UUID getUUID() {
        return player.getUniqueId();
    }

    /**
     * Kicks a player with the specified reason.
     *
     * @param reason The reason.
     */
    @Override
    public void kickPlayer(HammerText reason) {
        Text text = HammerTextConverter.constructMessage(reason);

        player.kick();
    }

    /**
     * Kicks a player with the specified reason.
     *
     * @param reason The reason.
     */
    @Override
    public void kickPlayer(String reason) {

    }
}
