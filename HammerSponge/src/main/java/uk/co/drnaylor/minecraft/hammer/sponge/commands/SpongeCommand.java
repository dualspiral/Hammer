package uk.co.drnaylor.minecraft.hammer.sponge.commands;

import com.google.common.base.Optional;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import uk.co.drnaylor.minecraft.hammer.core.commands.CommandCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

import java.util.Arrays;
import java.util.List;

/**
 * Executes a Sponge Command
 */
public class SpongeCommand implements CommandCallable {

    private final CommandCore core;
    private final Text error = Texts.of("[Hammer] An error occurred", TextColors.RED);

    public SpongeCommand(CommandCore core) {
        this.core = core;
    }

    @Override
    public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
        try {
            List<String> a = Arrays.asList(arguments.split(" "));
            if (source instanceof Player) {
                core.executeCommandAsPlayer(((Player) source).getUniqueId(), a);
            } else {
                core.executeCommandAsConsole(a);
            }

            return Optional.of(CommandResult.success());
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
        return Optional.absent();
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.absent();
    }

    @Override
    public Text getUsage(CommandSource source) {
        return HammerTextConverter.constructMessage(core.getUsageMessage());
    }
}
