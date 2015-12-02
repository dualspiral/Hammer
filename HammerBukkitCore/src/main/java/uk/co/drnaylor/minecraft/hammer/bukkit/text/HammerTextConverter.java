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
            ChatColor cf = HammerTextToCodeConverter.getCodeFromHammerText(f);
            if (cf != null) {
                sb.append(cf);
            }
        }
    }
}
