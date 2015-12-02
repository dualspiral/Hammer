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
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BanCheckRunnable implements Runnable {

    private final HammerCore core;

    public BanCheckRunnable(HammerCore core) {
        this.core = core;
    }

    @Override
    public void run() {
        // If we have no players, don't do a thing.
        List<WrappedPlayer> lwp = core.getWrappedServer().getOnlinePlayers();
        if (lwp.isEmpty()) {
            return;
        }

        // Get the server bans for any players currently online.
        Set<UUID> players = lwp.stream().map(WrappedPlayer::getUUID).collect(Collectors.toSet());

        List<HammerPlayerBan> bans;
        try (DatabaseConnection dg = core.getDatabaseConnection()){
            bans = dg.getBanHandler().getPlayerBansForServer(players, core.getConfig().getConfig().getNode("server", "id").getInt(1));
        } catch (Exception e) {
            // Let's not worry too much
            core.getWrappedServer().getLogger().warn("There was an error running the ban check task. It will be retried later.");
            e.printStackTrace();
            return;
        }

        if (bans.isEmpty()) {
            // No bans here!
            return;
        }

        final WrappedServer s = core.getWrappedServer();
        bans.forEach(b -> {
            WrappedPlayer wp = s.getPlayer(b.getBannedUUID());
            if (wp != null) {
                String name = wp.getName();

                // Ban in sync!
                s.getScheduler().runSyncNow(() -> wp.ban(s.getConsole(), b.getReason()));
                s.sendMessageToPermissionGroup(new HammerTextBuilder().add("[Hammer] The player ", HammerTextColours.RED)
                        .add(name, HammerTextColours.WHITE).add(" has been kicked as they have been banned from elsewhere.").build(), "hammer.notify");
                s.sendMessageToPermissionGroup(new HammerTextBuilder().add("[Hammer] Reason: " + b.getReason(), HammerTextColours.RED).build(), "hammer.notify");

            }
        });
    }
}
