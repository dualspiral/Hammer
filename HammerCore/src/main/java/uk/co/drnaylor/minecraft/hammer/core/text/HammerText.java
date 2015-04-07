package uk.co.drnaylor.minecraft.hammer.core.text;

public class HammerText {

    public final HammerTextColours colour;
    public final HammerTextFormats[] formats;
    public final String message;

    public HammerText(String message, HammerTextColours colour, HammerTextFormats[] formats) {
        this.formats = formats;
        this.colour = colour;
        this.message = message;
    }
}
