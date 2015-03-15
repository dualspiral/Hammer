package uk.co.drnaylor.minecraft.hammer.bukkit.commands;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.core.commands.CommandCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

/**
 * A Bukkit wrapper for the {@link CommandCore} object.
 */
public class BukkitCommand implements CommandExecutor {

    private final CommandCore core;

    public BukkitCommand(CommandCore core) {
        this.core = core;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        try {
            if (cs instanceof Player) {
                core.executeCommandAsPlayer(((Player)cs).getUniqueId(), Arrays.asList(strings));
            } else {
                core.executeCommandAsConsole(Arrays.asList(strings));
            }
        } catch (HammerException ex) {
            cs.sendMessage(ChatColor.RED + "[Hammer] An error occured while executing the command.");
            ex.printStackTrace();
        }

        return true;
    }
    
}
