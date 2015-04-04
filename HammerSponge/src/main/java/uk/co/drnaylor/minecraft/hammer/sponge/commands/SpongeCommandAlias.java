package uk.co.drnaylor.minecraft.hammer.sponge.commands;

import com.google.common.base.Optional;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;

import java.util.List;

public class SpongeCommandAlias implements CommandCallable {
    @Override
    public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException {
        return false;
    }

    @Override
    public boolean testPermission(CommandSource source) {
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
