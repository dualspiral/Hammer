package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;

public class ArgumentParseException extends Exception {

    private HammerText htmessage;

    public ArgumentParseException(String message) {
        super(message);
        htmessage = new HammerTextBuilder().add(message, HammerTextColours.RED).build();
    }

    public HammerText getHammerTextMessage() {
        return htmessage;
    }
}
