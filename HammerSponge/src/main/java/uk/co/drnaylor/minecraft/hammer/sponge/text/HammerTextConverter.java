package uk.co.drnaylor.minecraft.hammer.sponge.text;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextStyle;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextFormats;

import java.util.ArrayList;
import java.util.List;

public final class HammerTextConverter {

    private HammerTextConverter() { }

    /**
     * Constructs a message from the collection of {@link HammerText} messages.
     *
     * @param message The {@link HammerText} messages.
     * @return The completed message.
     */
    public static Text constructMessage(HammerText message) {
        TextBuilder builder = Texts.builder();

        // For each HammerText element...
        for (HammerText.Element e : message.getElements()) {
            // Message
            TextBuilder inner = Texts.builder(e.message);

            // Colour?
            if (e.colour != null) {
                inner.color(HammerTextToTextColorCoverter.getCodeFromHammerText(e.colour));
            }

            // Format?
            if (e.formats != null && e.formats.length > 0) {
                List<TextStyle> styles = new ArrayList<>();
                for (HammerTextFormats format : e.formats) {
                    styles.add(HammerTextToTextColorCoverter.getCodeFromHammerText(format));
                }
            }

            // Into the builder it goes.
            builder.append(inner.build());
        }

        // Build the builder and return it!
        return builder.build();
    }
}
