package uk.co.drnaylor.minecraft.hammer.core.interfaces;

import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.util.UUID;

public interface IPlayerTranslator {
    UUID playerNameToUUID(String name);

    String uuidToPlayerName(UUID uuid);

    /**
     * Gets a {@link WrappedPlayer} from a player name.
     *
     * @param name The player name. MUST be exact, MUST be online.
     * @return The player, or <code>null</code> if the player is not found.
     */
    WrappedPlayer nameToOnlinePlayer(String name);

    /**
     * Gets a {@link WrappedPlayer} from a player {@link UUID}.
     *
     * @param uuid The {@link UUID} of the player
     * @return The player, or <code>null</code> if the player is not found.
     */
    WrappedPlayer uuidToPlayer(UUID uuid);
}
