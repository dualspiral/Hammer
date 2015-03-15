package uk.co.drnaylor.minecraft.hammer.bukkit.commands;

import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Runs the Hammer Command.
 *
 * This command is specific to Bukkit.
 */
public class HammerCommand implements CommandExecutor {

    private final HammerBukkitPlugin plugin;

    /**
     * Initialises the command object.
     * @param plugin The {@link uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin}
     */
    public HammerCommand(HammerBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        return false;
    }
}
