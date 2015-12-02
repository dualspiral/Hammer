/*
 * This file is part of Hammer, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Daniel Naylor
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
        init();
    }

    public static void init() {
        htc.clear();
        htf.clear();

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
