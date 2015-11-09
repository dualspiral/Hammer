package uk.co.drnaylor.minecraft.hammer.core.commands;

import com.google.common.collect.Lists;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.runnables.BanCheckRunnable;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.util.List;

@RunAsync
public class UpdateBansCommandCore extends CommandCore {

    public UpdateBansCommandCore(HammerCore core) {
        super(core);

        this.permissionNodes.add("hammer.admin.updatebans");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        return Lists.newArrayList();
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
    }

    @Override
    protected boolean executeCommand(WrappedCommandSource source, ArgumentMap arguments, DatabaseConnection conn) throws HammerException {
        source.sendMessage(new HammerTextBuilder().add("[Hammer] Manual ban update has started.", HammerTextColours.GREEN).build());
        new BanCheckRunnable(core).run();
        source.sendMessage(new HammerTextBuilder().add("[Hammer] Manual ban update has completed.", HammerTextColours.GREEN).build());

        return true;
    }

    @Override
    protected String commandName() {
        return "updatebans";
    }
}
