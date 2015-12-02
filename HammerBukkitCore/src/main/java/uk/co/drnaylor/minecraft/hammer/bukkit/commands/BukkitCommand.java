/*
 * This file is part of Hammer, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Daniel Naylor
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.co.drnaylor.minecraft.hammer.bukkit.commands;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.bukkit.wrappers.BukkitWrappedConsole;
import uk.co.drnaylor.minecraft.hammer.bukkit.wrappers.BukkitWrappedPlayer;
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
                core.executeCommand(new BukkitWrappedPlayer((Player)cs), Arrays.asList(strings));
            } else if (cs instanceof ConsoleCommandSender) {
                core.executeCommand(new BukkitWrappedConsole((ConsoleCommandSender)cs), Arrays.asList(strings));
            }
        } catch (HammerException ex) {
            cs.sendMessage(ChatColor.RED + "[Hammer] An error occured while executing the command.");
            ex.printStackTrace();
        }

        return true;
    }
    
}
