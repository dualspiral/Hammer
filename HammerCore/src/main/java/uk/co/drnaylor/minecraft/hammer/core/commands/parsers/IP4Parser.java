package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Optional;
import java.util.regex.Pattern;

public class IP4Parser implements IParser<InetAddress> {
    private static final Pattern ipParser = Pattern.compile("^([0-2]?\\d{1,2}\\.){3}([0-2]?\\d{1,2})$");

    @Override
    public Optional<InetAddress> parseArgument(ListIterator<String> stringIterator) throws ArgumentParseException {
        if (!stringIterator.hasNext()) {
            throw new ArgumentParseException("No argument.");
        }

        String ip = stringIterator.next();
        if (!ipParser.matcher(ip).matches()
            && Arrays.asList(ip.split(".")).stream().map(Integer::parseInt).anyMatch(t -> t > 255)) {

            // Can't have more than 255!
            throw new ArgumentParseException("Could not get IP address from string " + ip);
        }

        try {
            return Optional.of(Inet4Address.getByName(ip));
        } catch (UnknownHostException e) {
            throw new ArgumentParseException("Could not get IP address from string " + ip);
        }
    }

    @Override
    public void onFailedButOptional(ListIterator<String> stringListIterator) {
        if (stringListIterator.hasPrevious()) {
            stringListIterator.previous();
        }
    }
}
