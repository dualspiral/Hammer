package uk.co.drnaylor.minecraft.hammer.bukkit.coreimpl;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerToUUIDTranslator;

public class BukkitPlayerToUUIDTranslator implements IPlayerToUUIDTranslator {

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
}
