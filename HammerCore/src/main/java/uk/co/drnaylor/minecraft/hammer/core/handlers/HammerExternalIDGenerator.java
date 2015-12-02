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
package uk.co.drnaylor.minecraft.hammer.core.handlers;

import java.math.BigInteger;
import java.security.SecureRandom;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

public class HammerExternalIDGenerator {
    private final DatabaseConnection core;
    private final SecureRandom random = new SecureRandom();

    HammerExternalIDGenerator(DatabaseConnection core) {
        this.core = core;
    }

    /**
     * Returns a random 8 character ID that has not been used in the DB.
     * @return The generated ID.
     * @throws uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException
     */
    public String generateExternalId() throws HammerException {
        String s;
        do {
            s = new BigInteger(70, random).toString(36);

            // If the string is too small, or already used, get another one.
        } while (s.length() < 8 || core.getBanHandler().isExternalIdUsed(s));

        return s.substring(0, 8);
    }
}
