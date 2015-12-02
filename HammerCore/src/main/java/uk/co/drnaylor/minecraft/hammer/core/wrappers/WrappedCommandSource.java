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
package uk.co.drnaylor.minecraft.hammer.core.wrappers;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

import java.util.UUID;

public interface WrappedCommandSource {
    /**
     * Gets the name of the source.
     *
     * @return The name.
     */
    String getName();

    /**
     * Gets the Unique Identifier of the source.
     *
     * @return The {@link UUID}
     */
    UUID getUUID();

    /**
     * Sends a message to the target
     *
     * @param message The message
     */
    void sendMessage(HammerText message);

    /**
     * Sends a message to the target
     *
     * @param message The message
     */
    void sendMessage(String message);

    /**
     * Gets whether the source has the specified permission
     *
     * @param permission The permission
     * @return <code>true</code> if the source has the permission specified.
     */
    boolean hasPermission(String permission);
}
