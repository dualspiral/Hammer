package uk.co.drnaylor.minecraft.hammer.core.text;

import java.util.ArrayList;
import java.util.Collection;

public class HammerTextBuilder {

    private ArrayList<HammerText> textList = new ArrayList<>();

    /**
     * Adds a string of text to the builder.
     *
     * @param text The message to send.
     * @param colour The colour to use. Defaults to RESET.
     * @param formats The formats to use. Defaults to an array of NONE only.
     * @return Returns the builder, for chaining.
     */
    public HammerTextBuilder addText(String text, HammerTextColours colour, HammerTextFormats... formats) {
        if (colour == null) {
            colour = HammerTextColours.RESET;
        }

        if (formats == null || formats.length == 0) {
            formats = new HammerTextFormats[] { HammerTextFormats.NONE };
        }

        textList.add(new HammerText(text, colour, formats));
        return this;
    }

    /**
     * Gets the current {@list Collection} of {@link HammerText} objects.
     *
     * @return Gets the current text.
     */
    public Collection<HammerText> getText() {
        return new ArrayList<>(textList);
    }

    /**
     * Clears the builder.
     */
    public void clear() {
        textList.clear();
    }
}
