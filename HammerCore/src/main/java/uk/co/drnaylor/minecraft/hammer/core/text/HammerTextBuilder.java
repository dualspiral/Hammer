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
