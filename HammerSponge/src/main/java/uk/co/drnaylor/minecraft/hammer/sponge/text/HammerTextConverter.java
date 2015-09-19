package uk.co.drnaylor.minecraft.hammer.sponge.text;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextFormats;

import java.util.ArrayList;
import java.util.List;

public final class HammerTextConverter {

    private HammerTextConverter() { }

    /**
     * Constructs a message from the collection of {@link HammerText} messages,
     * but without any styling.
     *
     * @param message The {@link HammerText} messages.
     * @return The completed {@link org.spongepowered.api.text.Text.Literal}
     */
    public static Text.Literal constructLiteral(HammerText message) {
        // Get the message, and just parse the actual text.
        StringBuilder sb = new StringBuilder();
        for (HammerText.Element e : message.getElements()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }

            sb.append(e.message);
        }

        return Texts.builder(sb.toString()).build();
    }

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
                TextColor c = HammerTextToTextColorCoverter.getCodeFromHammerText(e.colour);
                if (c != null) {
                    inner.color(c);
                }
            }

            // Format?
            if (e.formats != null && e.formats.length > 0) {
                List<TextStyle> styles = new ArrayList<>();
                for (HammerTextFormats format : e.formats) {
                    // Sometimes can be null...
                    TextStyle ts = HammerTextToTextColorCoverter.getCodeFromHammerText(format);
                    if (ts != null) {
                        styles.add(ts);
                    }
                }

                inner.style(styles.toArray(new TextStyle[styles.size()]));
            }

            // Into the builder it goes.
            builder.append(inner.build());
        }

        // Build the builder and return it!
        return builder.build();
    }
}
