package uk.co.drnaylor.minecraft.hammer.bukkit.text;

import org.bukkit.ChatColor;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextFormats;

public final class HammerTextConverter {

    private HammerTextConverter() { }

    /**
     * Constructs a message from the collection of {@link HammerText} messages.
     *
     * @param message The {@link HammerText} messages.
     * @return The completed message.
     */
    public static String constructMessage(HammerText message) {
        StringBuilder sb = new StringBuilder();

        for (HammerText.Element t : message.getElements()) {
            if (sb.length() > 0) {
                sb.append(ChatColor.RESET);
            }

            convertColour(t.colour, sb);
            convertFormats(t.formats, sb);

            sb.append(t.message);
        }

        return sb.toString();
    }

    private static void convertColour(HammerTextColours colour, StringBuilder sb) {
        ChatColor c = HammerTextToCodeConverter.getCodeFromHammerText(colour);
        if (c != null) {
            sb.append(c);
        }
    }

    private static void convertFormats(HammerTextFormats[] formats, StringBuilder sb) {
        for (HammerTextFormats f : formats) {
            if (f != null) {
                sb.append(HammerTextToCodeConverter.getCodeFromHammerText(f));
            }
        }
    }
}
