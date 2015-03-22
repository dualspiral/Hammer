package uk.co.drnaylor.minecraft.hammer.bukkit.coreimpl;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IServerMessageBuilder;

public class BukkitServerMessageBuilder implements IServerMessageBuilder {

    private ResourceBundle messageBundle = ResourceBundle.getBundle("messages", Locale.getDefault());

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

    @Override
    public void sendUnbanMessageToNotified(UUID bannee, UUID bannedBy, boolean allFlag) {
        OfflinePlayer pl = Bukkit.getOfflinePlayer(bannee);

        String name;
        if (bannedBy.equals(HammerConstants.consoleUUID)) {
            name = String.format("*%s*", messageBundle.getString("hammer.console"));
        } else {
            OfflinePlayer staff = Bukkit.getOfflinePlayer(bannedBy);
            if (staff.isOnline()) {
                name = staff.getPlayer().getDisplayName();
            } else {
                name = staff.getName();
            }
        }

        String playerName = messageBundle.getString("hammer.unknownPlayer");
        if (pl.hasPlayedBefore()) {
            playerName = pl.getName();
        }

        StringBuilder message = new StringBuilder().append(ChatColor.GREEN).append(HammerConstants.textTag)
                .append(" ").append(playerName).append(" ");

        if (allFlag) {
            message.append(messageBundle.getString("hammer.server.unbanAllServers"));
        } else {
            message.append(messageBundle.getString("hammer.server.unbanOneServer"));
        }

        message.append(" ").append(name);
        Bukkit.broadcast(message.toString(), "hammer.notify");
    }

    public String[] getMessageToSend(UUID banned, UUID bannedBy, String reason, boolean isTemp, boolean isAll, boolean isPerm) {
        OfflinePlayer pl = Bukkit.getOfflinePlayer(banned);

        String[] messages = new String[2];
        String name;
        if (bannedBy.equals(HammerConstants.consoleUUID)) {
            name = String.format("*%s*", messageBundle.getString("hammer.console"));
        } else {
            OfflinePlayer staff = Bukkit.getOfflinePlayer(bannedBy);
            if (staff.isOnline()) {
                name = staff.getPlayer().getDisplayName();
            } else {
                name = staff.getName();
            }
        }

        String modifier = "";
        if (isTemp) {
            modifier = messageBundle.getString("hammer.temporarily");
        } else if (isPerm) {
            modifier = messageBundle.getString("hammer.permanently");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ChatColor.RED).append(HammerConstants.textTag).append(" ").append(ChatColor.WHITE)
                .append(pl.getName()).append(ChatColor.RED)
                .append(" ").append(MessageFormat.format(messageBundle.getString("hammer.server.banMessage"), modifier))
                .append(ChatColor.WHITE).append(name);
        messages[0] = sb.toString();
        messages[1] = new StringBuffer().append(ChatColor.RED).append(HammerConstants.textTag)
            .append(" ").append(messageBundle.getString("hammer.reason")).append(" ").append(reason).toString();

        return messages;
    }
}
