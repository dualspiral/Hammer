package uk.co.drnaylor.minecraft.hammer.sponge.commands;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.source.ConsoleSource;
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
    private final Text error = Texts.of("[Hammer] An error occurred", TextColors.RED);
    private final Game game;

    public SpongeCommand(Game game, CommandCore core) {
        this.game = game;
        this.core = core;
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        try {
            List<String> a = Arrays.asList(arguments.split(" "));
            if (source instanceof Player) {
                core.executeCommand(new SpongeWrappedPlayer(game, (Player)source), a);
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
