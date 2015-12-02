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

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

public class PlayerHandler {
    private final IDatabaseGateway dg;

    public PlayerHandler(IDatabaseGateway dg) {
        this.dg = dg;
    }

    public void updatePlayer(UUID player, String name, String ip) throws HammerException {
        try {
            dg.updatePlayer(player, name, ip);
        } catch (Exception ex) {
            throw new HammerException("An error occurred updating the player.", ex);
        }
    }

    public void updatePlayers(Collection<HammerPlayerInfo> players) throws HammerException {
        try {
            for (HammerPlayerInfo pl : players) {
                dg.updatePlayer(pl.getUUID(), pl.getName(), pl.getIp());
            }
        } catch (Exception ex) {
            throw new HammerException("An error occurred updating the players.", ex);
        }
    }

    public HammerPlayerInfo getPlayer(UUID uuid) throws HammerException {
        try {
            return dg.getPlayerInfo(uuid);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the player.", ex);
        }
    }

    public List<HammerPlayerInfo> getPlayersByName(String name) throws Exception {
        try {
            return dg.getPlayerInfoFromName(name);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the players.", ex);
        }
    }

    public HammerPlayerInfo getLastPlayerByName(String name) throws Exception {
        try {
            return dg.getLastPlayerInfoFromName(name);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the player.", ex);
        }
    }
}
