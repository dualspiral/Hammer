package uk.co.drnaylor.minecraft.hammer.core.text;

import java.util.ArrayList;

public class HammerTextBuilder {

    private ArrayList<HammerText.Element> textList = new ArrayList<>();

    public HammerTextBuilder addText(String text) {
        return addText(text, null);
    }

    public HammerTextBuilder addText(String text, HammerTextColours colour) {
        return addText(text, colour);
    }

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

        textList.add(new HammerText.Element(text, colour, formats));
        return this;
    }

    /**
     * Clears the builder.
     */
    public HammerTextBuilder clear() {
        textList.clear();
        return this;
    }

    /**
     * Builds the {@link HammerText} object.
     *
     * @return The {@link HammerText} object.
     */
    public HammerText build() {
        return new HammerText(textList);
    }
}
