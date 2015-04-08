package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerTranslator;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.util.UUID;

public class SpongePlayerTranslator implements IPlayerTranslator {
    @Override
    public UUID playerNameToUUID(String name) {
        return null;
    }

    @Override
    public String uuidToPlayerName(UUID uuid) {
        return null;
    }

    /**
     * Gets a {@link WrappedPlayer} from a player name.
     *
     * @param name The player name. MUST be exact, MUST be online.
     * @return The player, or <code>null</code> if the player is not found.
     */
    @Override
    public WrappedPlayer nameToOnlinePlayer(String name) {
        return null;
    }

    /**
     * Gets a {@link WrappedPlayer} from a player {@link UUID}.
     *
     * @param uuid The {@link UUID} of the player
     * @return The player, or <code>null</code> if the player is not found.
     */
    @Override
    public WrappedPlayer uuidToPlayer(UUID uuid) {
        return null;
    }
}
