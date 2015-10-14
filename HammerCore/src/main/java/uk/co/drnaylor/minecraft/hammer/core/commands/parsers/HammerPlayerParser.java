package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class HammerPlayerParser implements IParser<List<HammerPlayerInfo>> {

    private final HammerCore core;

    public HammerPlayerParser(HammerCore core) {
        this.core = core;
    }

    @Override
    public Optional<List<HammerPlayerInfo>> parseArgument(ListIterator<String> stringIterator) throws ArgumentParseException {
        if (!stringIterator.hasNext()) {
            throw new ArgumentParseException("No player was specified");
        }

        try (DatabaseConnection dg = core.getDatabaseConnection()) {
            String name = stringIterator.next();
            List<HammerPlayerInfo> hpi = dg.getPlayerHandler().getPlayersByName(name);
            if (hpi == null || hpi.isEmpty()) {
                throw new ArgumentParseException("The player " + name + " does not exist in Hammer.");
            }

            return Optional.of(hpi);
        } catch (ArgumentParseException e) {
            // Special case, re-throw this.
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void onFailedButOptional(ListIterator<String> stringListIterator) {
        stringListIterator.previous();
    }
}
