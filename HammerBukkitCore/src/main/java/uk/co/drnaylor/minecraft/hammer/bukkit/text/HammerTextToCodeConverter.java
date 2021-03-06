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
package uk.co.drnaylor.minecraft.hammer.bukkit.text;

import org.bukkit.ChatColor;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextFormats;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides methods from converting from the server neutral {@link uk.co.drnaylor.minecraft.hammer.core.text.HammerText}
 * formats to Bukkit specific {@link ChatColor} objects.
 */
final class HammerTextToCodeConverter {
    private HammerTextToCodeConverter() { }

    private static final Map<HammerTextColours, ChatColor> htc = new HashMap<>();
    private static final Map<HammerTextFormats, ChatColor> htf = new HashMap<>();

    static  {
        htc.put(HammerTextColours.DARK_BLUE, ChatColor.DARK_BLUE);
        htc.put(HammerTextColours.DARK_AQUA, ChatColor.DARK_AQUA);
        htc.put(HammerTextColours.DARK_GRAY, ChatColor.DARK_GRAY);
        htc.put(HammerTextColours.DARK_GREEN, ChatColor.DARK_GREEN);
        htc.put(HammerTextColours.DARK_PURPLE, ChatColor.DARK_PURPLE);
        htc.put(HammerTextColours.DARK_RED, ChatColor.DARK_RED);
        htc.put(HammerTextColours.AQUA, ChatColor.AQUA);
        htc.put(HammerTextColours.BLACK, ChatColor.BLACK);
        htc.put(HammerTextColours.BLUE, ChatColor.BLUE);
        htc.put(HammerTextColours.GOLD, ChatColor.GOLD);
        htc.put(HammerTextColours.GRAY, ChatColor.GRAY);
        htc.put(HammerTextColours.GREEN, ChatColor.GREEN);
        htc.put(HammerTextColours.LIGHT_PURPLE, ChatColor.LIGHT_PURPLE);
        htc.put(HammerTextColours.RED, ChatColor.RED);
        htc.put(HammerTextColours.WHITE, ChatColor.WHITE);
        htc.put(HammerTextColours.YELLOW, ChatColor.YELLOW);
        htc.put(HammerTextColours.RESET, ChatColor.RESET);

        htf.put(HammerTextFormats.BOLD, ChatColor.BOLD);
        htf.put(HammerTextFormats.ITALIC, ChatColor.ITALIC);
        htf.put(HammerTextFormats.MAGIC, ChatColor.MAGIC);
        htf.put(HammerTextFormats.UNDERLINE, ChatColor.UNDERLINE);
    }

    /**
     * Gets the {@link ChatColor} from the {@link HammerTextColours} provided.
     *
     * @param code The {@link HammerTextColours} to convert.
     * @return The {@link ChatColor}.
     */
    public static ChatColor getCodeFromHammerText(HammerTextColours code) {
        return htc.get(code);
    }

    /**
     * Gets the {@link ChatColor} form the {@link HammerTextFormats} provided.
     *
     * @param code The {@link HammerTextFormats} to convert.
     * @return The {@link ChatColor}.
     */
    public static ChatColor getCodeFromHammerText(HammerTextFormats code) {
        return htf.get(code);
    }
}
