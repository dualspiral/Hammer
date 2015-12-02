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

import java.io.File;
import java.util.List;
import java.util.UUID;

public interface WrappedServer {

    /**
     * Gets a player by the {@link UUID}
     *
     * @param uuid The {@link UUID}
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    WrappedPlayer getPlayer(UUID uuid);

    /**
     * Gets a player by their last known name
     *
     * @param name The name
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    WrappedPlayer getPlayer(String name);

    /**
     * Gets all players that are currently online.
     *
     * @return A {@link List} of online players.
     */
    List<WrappedPlayer> getOnlinePlayers();

    /**
     * Gets the console command sender.
     *
     * @return The {@link WrappedCommandSource} that represents the console.
     */
    WrappedCommandSource getConsole();

    /**
     * Sends a message to the entire server.
     *
     * @param message The message to send.
     */
    void sendMessageToServer(HammerText message);

    /**
     * Sends a message to a permission group.
     *
     * @param message The message to send.
     * @param permission The permission group that should see it.
     */
    void sendMessageToPermissionGroup(HammerText message, String permission);

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    void kickAllPlayers(WrappedCommandSource source, String reason);

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    void kickAllPlayers(WrappedCommandSource source, HammerText reason);

    /**
     * Sets whether the whitelist is enabled or not.
     *
     * @param set <code>true</code> if so.
     */
    void setWhitelist(boolean set);

    /**
     * Gets an object that contains methods for running code on or off the main thread.
     *
     * @return The {@link WrappedScheduler}
     */
    WrappedScheduler getScheduler();

    /**
     * Gets an object that contains methods for sending messages to the logger.
     *
     * @return The {@link WrappedLogger}
     */
    WrappedLogger getLogger();

    /**
     * Gets the data folder for Hammer.
     *
     * @return The data folder.
     */
    File getDataFolder();

    /**
     * Gets the folder in which Hammer should write flat-file logs.
     *
     * @return The log folder.
     */
    File getLogFolder();
}
