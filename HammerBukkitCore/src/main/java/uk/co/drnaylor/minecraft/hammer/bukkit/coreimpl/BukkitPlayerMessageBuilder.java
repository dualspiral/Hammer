package uk.co.drnaylor.minecraft.hammer.bukkit.coreimpl;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IPlayerMessageBuilder;

public class BukkitPlayerMessageBuilder implements IPlayerMessageBuilder {

    @Override
    public void sendNoPermsMessage(UUID uuid) {
        Player pl = Bukkit.getPlayer(uuid);
        if (pl != null) {
            pl.sendMessage(ChatColor.RED + "[Hammer] You do not have permission to perform that type of ban.");
        }
    }

    @Override
    public void sendNoPlayerMessage(UUID uuid) {
        if (uuid.equals(HammerConstants.consoleUUID)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Hammer] That player cannot be found.");
        } else {
            Player pl = Bukkit.getPlayer(uuid);
            if (pl != null) {
                pl.sendMessage(ChatColor.RED + "[Hammer] That player cannot be found.");
            }
        }
    }

    @Override
    public void sendUsageMessage(UUID player, String message) {
        if (player.equals(HammerConstants.consoleUUID)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Hammer] Command Usage: " + ChatColor.YELLOW + message);
        } else {
            Player pl = Bukkit.getPlayer(player);
            if (pl != null) {
                pl.sendMessage(ChatColor.RED + "[Hammer] Command Usage: " + ChatColor.YELLOW + message);
            }
        }
    }

    @Override
    public void sendErrorMessage(UUID player, String message) {
        if (player.equals(HammerConstants.consoleUUID)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Hammer] " + message);
        } else {
            Player pl = Bukkit.getPlayer(player);
            if (pl != null) {
                pl.sendMessage(ChatColor.RED + "[Hammer] " + message);
            }
        }
    }

    @Override
    public void sendStandardMessage(UUID player, String message, boolean withTag) {
        StringBuilder sb = new StringBuilder();
        if (withTag) {
            sb.append(ChatColor.RED).append("[Hammer] ");
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
    public void sendAlreadyBannedMessage(UUID player) {
        if (player.equals(HammerConstants.consoleUUID)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Hammer] This player has already been banned to this level.");
        } else {
            Player pl = Bukkit.getPlayer(player);
            if (pl != null) {
                pl.sendMessage(ChatColor.RED + "[Hammer] This player has already been banned to this level.");
            }
        }
    }

    @Override
    public void sendAlreadyBannedFailMessage(UUID player) {
        if (player.equals(HammerConstants.consoleUUID)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Hammer] This player has already been banned, and Hammer does not know how to resolve this. Please unban before re-banning.");
        } else {
            Player pl = Bukkit.getPlayer(player);
            if (pl != null) {
                pl.sendMessage(ChatColor.RED + "[Hammer] This player has already been banned, and Hammer does not know how to resolve this. Please unban before re-banning.");
            }
        }
    }

    @Override
    public void sendToPermMessage(UUID player) {
        if (player.equals(HammerConstants.consoleUUID)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Hammer] This player has already been banned with a permanent ban elsewhere. Your global ban has been upgraded to include this.");
        } else {
            Player pl = Bukkit.getPlayer(player);
            if (pl != null) {
                pl.sendMessage(ChatColor.RED + "[Hammer] This player has already been banned with a permanent ban elsewhere. Your global ban has been upgraded to include this.");
            }
        }
    }

    @Override
    public void sendToAllMessage(UUID player) {
        if (player.equals(HammerConstants.consoleUUID)) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Hammer] This player has already been banned with a global ban. Your permanent ban has been upgraded to include this.");
        } else {
            Player pl = Bukkit.getPlayer(player);
            if (pl != null) {
                pl.sendMessage(ChatColor.RED + "[Hammer] This player has already been banned with a global ban. Your permanent ban has been upgraded to include this.");
            }
        }
    }
    
}
