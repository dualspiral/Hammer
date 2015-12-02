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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Element e : elements) {
            sb.append(e.message);
        }

        return sb.toString();
    }
}
