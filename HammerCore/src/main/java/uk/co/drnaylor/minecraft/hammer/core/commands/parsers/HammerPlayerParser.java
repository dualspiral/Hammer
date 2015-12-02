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

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class HammerPlayerParser implements IParser<List<HammerPlayerInfo>> {

    private final HammerCore core;

    public HammerPlayerParser(HammerCore core) {
        this.core = core;
    }

    @Override
    public Optional<List<HammerPlayerInfo>> parseArgument(ListIterator<String> stringIterator) throws ArgumentParseException {
        if (!stringIterator.hasNext()) {
            throw new ArgumentParseException("No player was specified");
        }

        try (DatabaseConnection dg = core.getDatabaseConnection()) {
            String name = stringIterator.next();
            List<HammerPlayerInfo> hpi = dg.getPlayerHandler().getPlayersByName(name);
            if (hpi == null || hpi.isEmpty()) {
                throw new ArgumentParseException("The player " + name + " does not exist in Hammer.");
            }

            return Optional.of(hpi);
        } catch (ArgumentParseException e) {
            // Special case, re-throw this.
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public void onFailedButOptional(ListIterator<String> stringListIterator) {
        stringListIterator.previous();
    }
}
