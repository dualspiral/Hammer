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

import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

public interface WrappedPlayer extends WrappedCommandSource {

    /**
     * Bans the player with the specified reason
     *
     * @param source The {@link WrappedCommandSource} that performed this ban
     * @param reason The reason
     */
    void ban(WrappedCommandSource source, HammerText reason);

    /**
     * Bans the player with the specified reason
     *
     * @param source The {@link WrappedCommandSource} that performed this ban
     * @param reason The reason
     */
    void ban(WrappedCommandSource source, String reason);

    /**
     * Unbans the player
     */
    void unban();

    /**
     * Gets whether the player is banned.
     *
     * @return Whether the player is banned or not.
     */
    boolean isBanned();

    /**
     * Kicks the player with the specified reason.
     *
     * @param reason The reason.
     */
    void kick(HammerText reason);

    /**
     * Kicks the player with the specified reason.
     *
     * @param reason The reason.
     */
    void kick(String reason);

    /**
     * Gets whether the player is online.
     *
     * @return <code>true</code> if the player is online, <code>false</code> otherwise.
     */
    boolean isOnline();

    /**
     * Gets the {@link HammerPlayerInfo} that represents this player.
     *
     * @return The {@link HammerPlayerInfo}
     */
    HammerPlayerInfo getHammerPlayer();
}
