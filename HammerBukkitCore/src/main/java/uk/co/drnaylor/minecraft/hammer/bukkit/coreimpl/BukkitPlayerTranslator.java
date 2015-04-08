package uk.co.drnaylor.minecraft.hammer.bukkit.coreimpl;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import uk.co.drnaylor.minecraft.hammer.bukkit.wrappers.BukkitWrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerTranslator;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

public class BukkitPlayerTranslator implements IPlayerTranslator {

    @Override
    @SuppressWarnings("deprecation")
    public UUID playerNameToUUID(String name) {
        OfflinePlayer pl = Bukkit.getOfflinePlayer(name);
        if (pl.hasPlayedBefore()) {
            return pl.getUniqueId();
        }

        return null;
    }

    @Override
    public String uuidToPlayerName(UUID uuid) {
        OfflinePlayer pl = Bukkit.getOfflinePlayer(uuid);
        if (pl.hasPlayedBefore()) {
            return pl.getName();
        }

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
        return BukkitWrappedPlayer.ofOnlinePlayer(name);
    }

    /**
     * Gets a {@link WrappedPlayer} from a player {@link UUID}.
     *
     * @param uuid The {@link UUID} of the player
     * @return The player, or <code>null</code> if the player is not found.
     */
    @Override
    public WrappedPlayer uuidToPlayer(UUID uuid) {
        return BukkitWrappedPlayer.of(uuid);
    }
}
