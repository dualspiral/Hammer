package uk.co.drnaylor.minecraft.hammer.core.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class HammerText {

    private final Collection<Element> elements;

    public HammerText(Collection<Element> e) {
        this.elements = new ArrayList<>(e);
    }

    public HammerText(Element... elements) {
        this.elements = Arrays.asList(elements);
    }

    public Collection<Element> getElements() {
        return elements;
    }

    public static class Element {
        public final HammerTextColours colour;
        public final HammerTextFormats[] formats;
        public final String message;

        public Element(String message, HammerTextColours colour, HammerTextFormats[] formats) {
            this.formats = formats;
            this.colour = colour;
            this.message = message;
        }
    }
}
