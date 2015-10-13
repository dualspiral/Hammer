package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.util.ListIterator;
import java.util.Optional;

public class OnlinePlayerParser implements IParser<WrappedPlayer> {
    private final HammerCore core;

    public OnlinePlayerParser(HammerCore core) {
        this.core = core;
    }

    @Override
    public Optional<WrappedPlayer> parseArgument(ListIterator<String> stringIterator) throws ArgumentParseException {
        if (!stringIterator.hasNext()) {
            return Optional.empty();
        }

        String name = stringIterator.next();
        WrappedPlayer pl = core.getWrappedServer().getPlayer(name);
        if (pl == null || pl.isOnline()) {
            throw new ArgumentParseException("The player " + name + " is not online!");
        }

        return Optional.of(pl);
    }

    @Override
    public void onFailedButOptional(ListIterator<String> stringListIterator) {
        stringListIterator.previous();
    }
}
