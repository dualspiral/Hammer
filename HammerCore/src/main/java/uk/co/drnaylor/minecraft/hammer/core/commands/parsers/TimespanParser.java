package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import java.util.ListIterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimespanParser implements IParser<Integer> {
    private final Pattern timeString = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?$");

    private final int hours = 60;
    private final int days = 24 * hours;
    private final int weeks = 7 * days;

    @Override
    public Optional<Integer> parseArgument(ListIterator<String> stringIterator) throws ArgumentParseException {
        String s = stringIterator.next();
        Matcher m = timeString.matcher(s);
        if (m.matches()) {
            int time = amount(m.group(2), weeks);
            time += amount(m.group(4), days);
            time += amount(m.group(6), hours);
            time += amount(m.group(8), 1);

            if (time > 0) {
                return Optional.of(time * 60);
            }
        }

        return Optional.empty();
    }

    private int amount(String g, int multipler) {
        if (g != null && g.length() > 0) {
            return multipler * Integer.parseInt(g);
        }

        return 0;
    }
}
