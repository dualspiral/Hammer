package uk.co.drnaylor.minecraft.hammer.bukkit.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkit;

public class BukkitAlias implements CommandExecutor {

    private final String targetCommand;
    private final List<String> argumentsPrepend;
    private final HammerBukkit plugin;

    public BukkitAlias(HammerBukkit plugin, String targetCommand, List<String> argumentsPrepend) {
        this.targetCommand = targetCommand;
        this.argumentsPrepend = argumentsPrepend;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        ArrayList<String> s = new ArrayList<>();
        s.addAll(argumentsPrepend);
        s.addAll(Arrays.asList(strings));
        Command c = plugin.getCommand(targetCommand);
        return c.execute(cs, targetCommand, s.toArray(new String[0]));
    }
}
