package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.List;
import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;

public class UnbanIPCommandCore extends CommandCore {

    public UnbanIPCommandCore(HammerCore core) {
        super(core);
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
    }

    @Override
    protected boolean executeCommand(UUID playerUUID, List<String> arguments, boolean isConsole, DatabaseConnection conn) throws HammerException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HammerText getUsageMessage() {
        return new HammerTextBuilder().add("/ipunban ip", HammerTextColours.YELLOW).build();
    }

}
