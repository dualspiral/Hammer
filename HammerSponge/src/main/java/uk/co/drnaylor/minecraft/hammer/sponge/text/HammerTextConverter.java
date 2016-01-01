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

import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
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
     * @return The completed {@link org.spongepowered.api.text.LiteralText}
     */
    public static LiteralText constructLiteral(HammerText message) {
        // Get the message, and just parse the actual text.
        StringBuilder sb = new StringBuilder();
        for (HammerText.Element e : message.getElements()) {
            if (sb.length() > 0) {
                sb.append(" ");
            }

            sb.append(e.message);
        }

        return Text.builder(sb.toString()).build();
    }

    /**
     * Constructs a message from the collection of {@link HammerText} messages.
     *
     * @param message The {@link HammerText} messages.
     * @return The completed message.
     */
    public static Text constructMessage(HammerText message) {
        Text.Builder builder = Text.builder();

        // For each HammerText element...
        for (HammerText.Element e : message.getElements()) {
            // Message
            Text.Builder inner = Text.builder(e.message);

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
