package uk.co.drnaylor.minecraft.hammer.core.commands;

import com.google.common.collect.Lists;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.io.IOException;
import java.util.List;

@RunAsync
public class ReloadCommandCore extends CommandCore {

    private final HammerText txt =
            new HammerTextBuilder().add("[Hammer] Configuration reloaded. Any database changes will require a server restart.", HammerTextColours.GREEN).build();

    public ReloadCommandCore(HammerCore core) {
        super(core);

        this.permissionNodes.add("hammer.admin.reload");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        return Lists.newArrayList();
    }

    @Override
    protected boolean requiresDatabase() {
        return false;
    }

    @Override
    protected boolean executeCommand(WrappedCommandSource source, ArgumentMap arguments, DatabaseConnection conn) throws HammerException {
        try {
            core.reloadConfig();
            source.sendMessage(txt);
        } catch (IOException e) {
            e.printStackTrace();
            throw new HammerException("Reload failed.", e);
        }

        return true;
    }

    @Override
    protected String commandName() {
        return "hammerreload";
    }
}
