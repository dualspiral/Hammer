package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import uk.co.drnaylor.minecraft.hammer.core.commands.enums.FlagEnum;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FlagParser<T extends Enum<T> & FlagEnum> implements IParser<List<T>> {

    private final Pattern regexToMatch;
    private final EnumSet<T> enums;

    public FlagParser(Class<T> clazz) {
        // Cache the flags into a regex "-(a|b|c)
        StringBuilder regex = new StringBuilder("^-([");
        enums = EnumSet.allOf(clazz);
        enums.forEach(e -> e.getStrings().forEach(regex::append));
        regex.deleteCharAt(regex.length() - 1).append("]+)$");
        regexToMatch = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE);
    }

    @Override
    public Optional<List<T>> parseArgument(ListIterator<String> stringIterator) throws ArgumentParseException {
        if (!stringIterator.hasNext()) {
            return Optional.empty();
        }

        List<T> list = new ArrayList<>();
        boolean cont = true;

        do {
            String arg = stringIterator.next();
            Matcher m = regexToMatch.matcher(arg);
            cont = m.matches();
            if (cont) {
                // Get each letter and get the enum from it.
                for (char c : m.group(1).toCharArray()) {
                    // There will be one, by very virtue of the construction of this object.
                    list.add(enums.stream().filter(e -> e.getStrings().contains(c)).findFirst().get());
                }
            }
        } while (cont);

        // Reverse the iterator.
        stringIterator.previous();

        if (list.isEmpty()) {
            throw new ArgumentParseException("No flags were specified! Allowed flags: " + enums.stream().map(FlagEnum::getStrings).map(Object::toString).sorted().collect(Collectors.joining(" ")));
        }

        return Optional.of(list);
    }
}
