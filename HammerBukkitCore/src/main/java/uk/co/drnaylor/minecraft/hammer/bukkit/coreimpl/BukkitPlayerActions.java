package uk.co.drnaylor.minecraft.hammer.bukkit.coreimpl;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerActions;

public class BukkitPlayerActions implements IPlayerActions {

    /**
     * Kicks a player.
     *
     * @param player The UUID of the player to kick.
     * @param kicker The UUID of the player doing the kick.
     * @param reason The reason the player is being given the boot.
     *
     * @deprecated Use the {@link uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer}
     * implementation instead.
     */
    @Override
    @Deprecated
    public void kickPlayer(UUID player, UUID kicker, String reason) {
        Bukkit.getPlayer(player).kickPlayer(reason);
    }

    /**
     * Kicks all players but the kicker.
     *
     * @param kicker The UUID of the player doing the kick.
     * @param reason The reason the players are being given the boot.
     */
    @Override
    public void kickAllPlayers(UUID kicker, String reason) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (!pl.getUniqueId().equals(kicker)) {
                pl.kickPlayer(reason);
            }
        }
    }

    /**
     * Bans a player.
     * @param player The UUID of the player to ban.
     * @param banner The UUID of the player doing the ban.
     * @param reason The reason the player is being given the boot.
     */
    @Override
    @SuppressWarnings("deprecation")
    public void banPlayer(UUID player, UUID banner, String reason) {
        StringBuilder sb = new StringBuilder();
        sb.append("Banned by: ").append(getName(banner)).append("\n");
        sb.append("Reason: ").append(reason);

        OfflinePlayer pl = Bukkit.getOfflinePlayer(player);
        if (pl.isOnline()) {
            pl.getPlayer().kickPlayer(sb.toString());
        }

        // I know this is deprecated, but why Bukkit's newer Ban API was pulled, I'll never know
        // The documentation for it is pretty shoddy, I don't even think it uses UUIDs.
        // Maybe I'll go and improve it one day. Depends if I'm really bored.
        //
        // We're storing the reason anyway, so we might as well just go ahead and use this much 
        // easier to understand version.
        pl.setBanned(true);
    }

    /**
     * Unbans a player.
     * @param player The {@link UUID} of the player to unban.
     */
    @Override
    public void unbanPlayer(UUID player) {
        // Hey, if we're going to use "the wrong thing", we might as well be consistent about it.
        Bukkit.getOfflinePlayer(player).setBanned(false);
    }

    private String getName(UUID player) {
        if (player.equals(HammerConstants.consoleUUID)) {
            return "*Console*";
        }

        return Bukkit.getOfflinePlayer(player).getName();
    }
}
