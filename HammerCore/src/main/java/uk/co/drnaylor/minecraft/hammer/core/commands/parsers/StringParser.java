package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import java.util.*;

public class StringParser implements IParser<String> {
    private final boolean toEnd;

    public StringParser(boolean toEnd) {
        this.toEnd = toEnd;
    }

    @Override
    public Optional<String> parseArgument(ListIterator<String> stringIterator) throws ArgumentParseException {
        if (!stringIterator.hasNext()) {
            throw new ArgumentParseException("Not specified!");
        }

        if (!toEnd) {
            return Optional.of(stringIterator.next());
        }

        List<String> strings = new ArrayList<>();
        stringIterator.forEachRemaining(strings::add);
        return Optional.of(String.join(" ", strings));
    }

}
