package uk.co.drnaylor.minecraft.hammer.sponge.commands;

import com.google.common.base.Optional;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandSource;

import java.util.List;

public class HammerCommand implements CommandCallable {
    @Override
    public boolean call(CommandSource source, String arguments, List<String> parents) throws CommandException {
        return false;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return false;
    }

    @Override
    public Optional<String> getShortDescription() {
        return null;
    }

    @Override
    public Optional<String> getHelp() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return null;
    }
}
