package uk.co.drnaylor.minecraft.hammer.bukkit.coreimpl;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerMessageBuilder;

public class BukkitPlayerMessageBuilder implements IPlayerMessageBuilder {

    private final ResourceBundle messageBundle = ResourceBundle.getBundle("messages", Locale.getDefault());

    private StringBuilder getMessage(String messageKey) {
        StringBuilder sb = new StringBuilder().append(ChatColor.RED).append(HammerConstants.textTag).append(" ");
        if (messageKey != null) {
            sb.append(messageBundle.getString(messageKey));
        }

        return sb;
    }

    private void sendMessage(UUID uuid, String messageKey) {
        String msg = getMessage(messageKey).toString();
        if (uuid.equals(HammerConstants.consoleUUID)) {
            Bukkit.getConsoleSender().sendMessage(msg);
        } else {
            Player pl = Bukkit.getPlayer(uuid);
            if (pl != null) {
                pl.sendMessage(msg);
            }
        }
    }

    @Override
    public void sendNoPermsMessage(UUID uuid) {
        sendMessage(uuid, "hammer.player.noperms");
    }

    @Override
    public void sendNoPlayerMessage(UUID uuid) {
        sendMessage(uuid, "hammer.player.noplayer");
    }

    @Override
    public void sendUsageMessage(UUID player, String message) {
        String m = getMessage("hammer.player.commandUsage").append(" ").append(ChatColor.YELLOW).append(message).toString();
        if (player.equals(HammerConstants.consoleUUID)) {
            Bukkit.getConsoleSender().sendMessage(m);
        } else {
            Player pl = Bukkit.getPlayer(player);
            if (pl != null) {
                pl.sendMessage(m);
            }
        }
    }

    @Override
    public void sendErrorMessage(UUID player, String message) {
        String m = getMessage(null).append(" ").append(message).toString();
        if (player.equals(HammerConstants.consoleUUID)) {
            Bukkit.getConsoleSender().sendMessage(m);
        } else {
            Player pl = Bukkit.getPlayer(player);
            if (pl != null) {
                pl.sendMessage(m);
            }
        }
    }

    @Override
    public void sendStandardMessage(UUID player, String message, boolean withTag) {
        StringBuilder sb;
        if (withTag) {
            sb = getMessage(null).append(" ");
        } else {
            sb = new StringBuilder();
        }

        sb.append(ChatColor.GREEN).append(message);
        if (player.equals(HammerConstants.consoleUUID)) {
            Bukkit.getConsoleSender().sendMessage(sb.toString());
        } else {
            Player pl = Bukkit.getPlayer(player);
            if (pl != null) {
                pl.sendMessage(sb.toString());
            }
        }
    }

    @Override
    public void sendAlreadyBannedMessage(UUID uuid) {
        sendMessage(uuid, "hammer.player.alreadyBanned");
    }

    @Override
    public void sendAlreadyBannedFailMessage(UUID uuid) {
        sendMessage(uuid, "hammer.player.ambiguousBan");
    }

    @Override
    public void sendToPermMessage(UUID uuid) {
        sendMessage(uuid, "hammer.player.upgradeToPerm");
    }

    @Override
    public void sendToAllMessage(UUID uuid) {
        sendMessage(uuid, "hammer.player.upgradeToAll");
    }
    
}
