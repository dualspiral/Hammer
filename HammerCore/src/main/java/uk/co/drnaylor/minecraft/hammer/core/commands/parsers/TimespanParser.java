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
package uk.co.drnaylor.minecraft.hammer.core.commands.parsers;

import java.util.ListIterator;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimespanParser implements IParser<Integer> {
    private final Pattern timeString = Pattern.compile("^((\\d+)w)?((\\d+)d)?((\\d+)h)?((\\d+)m)?$");

    private final int hours = 60;
    private final int days = 24 * hours;
    private final int weeks = 7 * days;

    @Override
    public Optional<Integer> parseArgument(ListIterator<String> stringIterator) throws ArgumentParseException {
        if (!stringIterator.hasNext()) {
            throw new ArgumentParseException("No time was specified");
        }

        String s = stringIterator.next();
        Matcher m = timeString.matcher(s);
        if (m.matches()) {
            int time = amount(m.group(2), weeks);
            time += amount(m.group(4), days);
            time += amount(m.group(6), hours);
            time += amount(m.group(8), 1);

            if (time > 0) {
                return Optional.of(time * 60);
            }
        }

        throw new ArgumentParseException("Could not parse " + s + " - must use w, d, h and/or m in that order.");
    }

    private int amount(String g, int multipler) {
        if (g != null && g.length() > 0) {
            return multipler * Integer.parseInt(g);
        }

        return 0;
    }
}
