package uk.co.drnaylor.minecraft.hammer.sponge.commands;

import com.google.common.base.Optional;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;
import uk.co.drnaylor.minecraft.hammer.core.commands.CommandCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class SpongeCommand implements CommandCallable {

    private final CommandCore commandCore;
    private final Collection<String> basePermissions;
    private final Optional<String> description;

    public SpongeCommand(CommandCore commandCore, Collection<String> basePermissions, String description) {
        this.commandCore = commandCore;
        this.basePermissions = basePermissions;
        this.description = Optional.of(description);
    }

    @Override
    public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException {
        try {
            if (source instanceof Player) {
                commandCore.executeCommandAsPlayer(((Player) source).getUniqueId(), Arrays.asList(arguments.split(" ")));
            } else {
                commandCore.executeCommandAsConsole(Arrays.asList(arguments.split(" ")));
            }
        } catch (HammerException ex) {
            source.sendMessage(Texts.of("[Hammer] An error occurred while executing the command.").
                    builder().color(TextColors.RED).build());
            ex.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        for (String permission : basePermissions) {
            if (source.hasPermission(permission)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String getShortDescription(CommandSource commandSource) {
        return null;
    }

    @Override
    public Text getHelp(CommandSource commandSource) {
        return null;
    }

    @Override
    public String getUsage(CommandSource commandSource) {
        return null;
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return null;
    }
}
