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
package uk.co.drnaylor.minecraft.hammer.core.runnables;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class HammerPlayerUpdateRunnable implements Runnable {
    private final Set<HammerPlayerInfo> player;
    private final HammerCore core;

    public HammerPlayerUpdateRunnable(HammerCore core) {
        this.core = core;
        this.player = new HashSet<>();
    }

    public void addPlayer(WrappedPlayer pl) {
        HammerPlayerInfo hp = pl.getHammerPlayer();

        if (hp != null) {
            player.add(pl.getHammerPlayer());
        }
    }

    @Override
    public void run() {
        if (player.isEmpty()) {
            return;
        }

        // In case a new player comes in, we transfer the players to a second set.
        // This way, new players who weren't sent to the DB won't get wiped.
        HashSet<HammerPlayerInfo> hpi = new HashSet<>();
        hpi.addAll(player);
        player.clear();

        // Process them on the async thread!
        try (DatabaseConnection conn = core.getDatabaseConnection()) {
            conn.getPlayerHandler().updatePlayers(hpi);
        } catch (Exception ex) {
            Logger.getAnonymousLogger().warning("Could not update Hammer with latest player information. Will try again during the next cycle.");

            // Put them back, we'll try again.
            player.addAll(hpi);
        }
    }
}
