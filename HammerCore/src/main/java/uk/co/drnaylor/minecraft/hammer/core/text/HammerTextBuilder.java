package uk.co.drnaylor.minecraft.hammer.core.text;

import java.util.ArrayList;

public class HammerTextBuilder {

    private final ArrayList<HammerText.Element> textList = new ArrayList<>();

    public HammerTextBuilder add(String text) {
        return add(text, null);
    }

    public HammerTextBuilder add(String text, HammerTextColours colour) {
        return add(text, colour, new HammerTextFormats[0]);
    }

    /**
     * Adds a string of text to the builder.
     *
     * @param text The message to send.
     * @param colour The colour to use. Defaults to RESET.
     * @param formats The formats to use. Defaults to an array of NONE only.
     * @return Returns the builder, for chaining.
     */
    private HammerTextBuilder add(String text, HammerTextColours colour, HammerTextFormats... formats) {
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
     * Adds a {@link uk.co.drnaylor.minecraft.hammer.core.text.HammerText.Element} to the builder
     *
     * @param element The element to add.
     * @return Returns this builder, for chaining.
     */
    public HammerTextBuilder add(HammerText.Element element) {
        textList.add(element);
        return this;
    }

    /**
     * Adds {@link uk.co.drnaylor.minecraft.hammer.core.text.HammerText} to the builder
     *
     * @param element The element to add.
     * @return Returns this builder, for chaining.
     */
    public HammerTextBuilder add(HammerText element) {
        textList.addAll(element.getElements());
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
