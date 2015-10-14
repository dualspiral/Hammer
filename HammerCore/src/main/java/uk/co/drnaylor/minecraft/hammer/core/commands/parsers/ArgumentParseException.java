package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;

public class ArgumentParseException extends Exception {

    private final HammerText htmessage;
    private final String argName;
    private boolean dontSkip = false;

    public ArgumentParseException(String message, boolean dontSkip) {
        this(message);
        this.dontSkip = dontSkip;
    }

    public ArgumentParseException(String message) {
        this(null, message);
    }

    public ArgumentParseException(String key, String message) {
        super(message);

        HammerTextBuilder htb = new HammerTextBuilder();
        if (key != null) {
            htb.add("Argument " + key + ": ", HammerTextColours.RED);
        }

        htmessage = htb.add(message, HammerTextColours.RED).build();
        argName = key;
    }

    public ArgumentParseException(String key, ArgumentParseException ex) {
        super(ex.getMessage(), ex);

        HammerTextBuilder htb = new HammerTextBuilder();
        if (key != null) {
            htb.add("Argument " + key + ": ", HammerTextColours.RED);
        }

        htmessage = htb.add(ex.getMessage(), HammerTextColours.RED).build();
        argName = key;
    }

    public HammerText getHammerTextMessage() {
        return htmessage;
    }

    public boolean isDontSkip() {
        return dontSkip;
    }
}
