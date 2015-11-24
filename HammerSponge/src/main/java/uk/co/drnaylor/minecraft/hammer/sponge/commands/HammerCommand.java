package uk.co.drnaylor.minecraft.hammer.sponge.commands;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;

import java.util.ArrayList;
import java.util.Collection;

public class HammerCommand implements CommandExecutor {

    private final Collection<Text> texts;

    public HammerCommand(HammerSponge plugin) {
        texts = new ArrayList<>();
        texts.add(Texts.of("This server is running Hammer for Sponge version " + HammerSponge.VERSION, TextColors.GREEN));
        texts.add(Texts.of("Using HammerCore version " + plugin.getCore().getHammerCoreVersion(), TextColors.GREEN));
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        src.sendMessages(texts);
        return CommandResult.success();
    }
}
