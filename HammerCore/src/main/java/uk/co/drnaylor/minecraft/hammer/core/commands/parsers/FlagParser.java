package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import uk.co.drnaylor.minecraft.hammer.core.commands.enums.FlagEnum;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FlagParser<T extends Enum<T> & FlagEnum> implements IParser<List<T>> {

    private final Pattern regexToMatch;
    private final String chars;
    private final EnumSet<T> enums;
    private final String flagEntries;

    public FlagParser(Class<T> clazz) {
        // Cache the flags into a regex "-(a|b|c)
        StringBuilder regex = new StringBuilder("^-([");
        enums = EnumSet.allOf(clazz);
        Set<Character> s = new HashSet<>();
        enums.forEach(e -> s.addAll(e.getStrings()));
        s.forEach(c -> regex.append(c.toString().toLowerCase()));
        flagEntries = s.stream().sorted().map(Object::toString).collect(Collectors.joining(" "));
        regex.deleteCharAt(regex.length() - 1).append("]+)$");
        regexToMatch = Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE);
        chars = "[-" + flagEntries.replaceAll(" ", "") + "]";
    }

    @Override
    public Optional<List<T>> parseArgument(ListIterator<String> stringIterator) throws ArgumentParseException {
        if (!stringIterator.hasNext()) {
            throw new ArgumentParseException("No flags were specified.");
        }

        List<T> list = new ArrayList<>();
        boolean cont;
        boolean rev = true;

        String arg = stringIterator.next();
        while (arg.startsWith("-")) {
            Matcher m = regexToMatch.matcher(arg);
            cont = m.matches();
            if (cont) {
                // Get each letter and get the enum from it.
                for (char c : m.group(1).toCharArray()) {
                    // There will be one, by very virtue of the construction of this object.
                    list.add(enums.stream().filter(e -> e.getStrings().contains(c)).findFirst().get());
                }
            } else {
                String non = arg.replaceAll(chars, "").replaceAll(".(?=.)", "$0 ");
                throw new ArgumentParseException("Unrecognised flags - " + non, true);
            }

            if (stringIterator.hasNext()) {
                arg = stringIterator.next();
            } else {
                rev = false;
                break;
            }
        }

        // Reverse the iterator.
        if (rev) {
            stringIterator.previous();
        }

        if (list.isEmpty()) {
            throw new ArgumentParseException("No flags were specified! Allowed flags: " + flagEntries);
        }

        return Optional.of(list);
    }

    public String getFlagEntries() {
        return "-" + flagEntries.replaceAll(" ", "");
    }
}
