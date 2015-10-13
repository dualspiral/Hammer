package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import java.util.ListIterator;
import java.util.Optional;

public interface IParser<T> {

    Optional<T> parseArgument(ListIterator<String> stringIterator) throws ArgumentParseException;

    default void onFailedButOptional(ListIterator<String> stringListIterator) {
        // Nothing
    }
}
