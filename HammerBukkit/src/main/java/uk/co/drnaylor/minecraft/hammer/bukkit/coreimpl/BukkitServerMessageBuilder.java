package uk.co.drnaylor.minecraft.hammer.bukkit.coreimpl;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IServerMessageBuilder;

public class BukkitServerMessageBuilder implements IServerMessageBuilder {

    @Override
    public void sendBanMessageToNotified(UUID banned, UUID bannedBy, String reason, boolean isTemp, boolean isAll, boolean isPerm) {
        String[] msg = getMessageToSend(banned, bannedBy, reason, isTemp, isAll, isPerm);
        Bukkit.broadcast(msg[0], "hammer.notify");
        Bukkit.broadcast(msg[1], "hammer.notify");
    }

    @Override
    public void sendBanMessageToAll(UUID banned, UUID bannedBy, String reason, boolean isTemp, boolean isAll, boolean isPerm) {
        String[] msg = getMessageToSend(banned, bannedBy, reason, isTemp, isAll, isPerm);
        Bukkit.broadcastMessage(msg[0]);
        Bukkit.broadcastMessage(msg[1]);
    }

    public String[] getMessageToSend(UUID banned, UUID bannedBy, String reason, boolean isTemp, boolean isAll, boolean isPerm) {
        OfflinePlayer pl = Bukkit.getOfflinePlayer(banned);
        
        String[] messages = new String[2];
        String name;
        if (bannedBy.equals(HammerConstants.consoleUUID)) {
            name = "*Console*";
        } else {
            OfflinePlayer staff = Bukkit.getOfflinePlayer(bannedBy);
            if (staff.isOnline()) {
                name = staff.getPlayer().getDisplayName();
            } else {
                name = staff.getName();
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.RED).append("[Hammer] ").append(ChatColor.WHITE).append(pl.getName()).append(ChatColor.RED).append(" was ");

        if (isTemp) {
            sb.append("temporarily ");
        } else if (isPerm) {
            sb.append("permanently ");
        }

        sb.append("banned by ").append(ChatColor.WHITE).append(name);
        messages[0] = sb.toString();
        messages[1] = ChatColor.RED + "[Hammer] Reason: " + reason;

        return messages;
    }

    @Override
    public void sendUnbanMessageToNotified(UUID bannee, UUID bannedBy, boolean allFlag) {
        OfflinePlayer pl = Bukkit.getOfflinePlayer(bannee);

        String name;
        if (bannedBy.equals(HammerConstants.consoleUUID)) {
            name = "*Console*";
        } else {
            OfflinePlayer staff = Bukkit.getOfflinePlayer(bannedBy);
            if (staff.isOnline()) {
                name = staff.getPlayer().getDisplayName();
            } else {
                name = staff.getName();
            }
        }

        String playerName = "Unknown Player";
        if (pl.hasPlayedBefore()) {
            playerName = pl.getName();
        }

        String message = ChatColor.GREEN + "[Hammer] " + playerName + " has been unbanned from " + (allFlag ? "all " : "this ") + "server" + (allFlag ? "s by " : " by ") + name;
        Bukkit.broadcast(message, "hammer.notify");
    }
}
