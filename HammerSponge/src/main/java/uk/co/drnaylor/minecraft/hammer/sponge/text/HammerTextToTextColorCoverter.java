package uk.co.drnaylor.minecraft.hammer.sponge.text;

import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextFormats;

import java.util.HashMap;
import java.util.Map;

public class HammerTextToTextColorCoverter {
    private HammerTextToTextColorCoverter() { }

    private static final Map<HammerTextColours, TextColor> htc = new HashMap<>();
    private static final Map<HammerTextFormats, TextStyle> htf = new HashMap<>();

    static  {
        htc.put(HammerTextColours.DARK_BLUE, TextColors.DARK_BLUE);
        htc.put(HammerTextColours.DARK_AQUA, TextColors.DARK_AQUA);
        htc.put(HammerTextColours.DARK_GRAY, TextColors.DARK_GRAY);
        htc.put(HammerTextColours.DARK_GREEN, TextColors.DARK_GREEN);
        htc.put(HammerTextColours.DARK_PURPLE, TextColors.DARK_PURPLE);
        htc.put(HammerTextColours.DARK_RED, TextColors.DARK_RED);
        htc.put(HammerTextColours.AQUA, TextColors.AQUA);
        htc.put(HammerTextColours.BLACK, TextColors.BLACK);
        htc.put(HammerTextColours.BLUE, TextColors.BLUE);
        htc.put(HammerTextColours.GOLD, TextColors.GOLD);
        htc.put(HammerTextColours.GRAY, TextColors.GRAY);
        htc.put(HammerTextColours.GREEN, TextColors.GREEN);
        htc.put(HammerTextColours.LIGHT_PURPLE, TextColors.LIGHT_PURPLE);
        htc.put(HammerTextColours.RED, TextColors.RED);
        htc.put(HammerTextColours.WHITE, TextColors.WHITE);
        htc.put(HammerTextColours.YELLOW, TextColors.YELLOW);
        htc.put(HammerTextColours.RESET, TextColors.RESET);

        htf.put(HammerTextFormats.BOLD, TextStyles.BOLD);
        htf.put(HammerTextFormats.ITALIC, TextStyles.ITALIC);
        htf.put(HammerTextFormats.MAGIC, TextStyles.OBFUSCATED);
        htf.put(HammerTextFormats.UNDERLINE, TextStyles.UNDERLINE);
    }

    /**
     * Gets the {@link TextColor} from the {@link HammerTextColours} provided.
     *
     * @param code The {@link HammerTextColours} to convert.
     * @return The {@link TextColor}.
     */
    public static TextColor getCodeFromHammerText(HammerTextColours code) {
        return htc.get(code);
    }

    /**
     * Gets the {@link TextStyle} form the {@link HammerTextFormats} provided.
     *
     * @param code The {@link HammerTextFormats} to convert.
     * @return The {@link TextStyle}.
     */
    public static TextStyle getCodeFromHammerText(HammerTextFormats code) {
        return htf.get(code);
    }
}
