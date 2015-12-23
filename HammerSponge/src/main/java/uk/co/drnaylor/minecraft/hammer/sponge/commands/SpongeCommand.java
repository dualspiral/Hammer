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
package uk.co.drnaylor.minecraft.hammer.sponge.commands;

import org.spongepowered.api.command.*;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import uk.co.drnaylor.minecraft.hammer.core.commands.CommandCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.sponge.wrappers.SpongeWrappedConsole;
import uk.co.drnaylor.minecraft.hammer.sponge.wrappers.SpongeWrappedPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Executes a Sponge Command
 */
public class SpongeCommand implements CommandCallable {

    private final CommandCore core;
    private final Text error = Text.of("[Hammer] An error occurred", TextColors.RED);

    public SpongeCommand(CommandCore core) {
        this.core = core;
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            throw new CommandPermissionException(Text.of(TextColors.RED, "[Hammer] You do not have permission to do that."));
        }

        try {
            List<String> a = Arrays.asList(arguments.split(" "));
            if (source instanceof Player) {
                core.executeCommand(new SpongeWrappedPlayer((Player)source), a);
            } else if (source instanceof ConsoleSource) {
                core.executeCommand(new SpongeWrappedConsole((ConsoleSource)source), a);
            }

            return CommandResult.success();
        } catch (HammerException e) {
            e.printStackTrace();
            throw new CommandException(error, e);
        }

    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return null;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        for (String p : core.getRequiredPermissions()) {
            if (source.hasPermission(p)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.empty();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return HammerTextConverter.constructMessage(core.getUsageMessage());
    }
}
