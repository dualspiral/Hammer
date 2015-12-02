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
package uk.co.drnaylor.minecraft.hammer.core.data;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;

/**
 * Represents data from the Hammer Data store.
 */
public class HammerPlayerInfo implements Comparable<HammerPlayerInfo> {
    private final UUID uuid;
    private final String name;
    private final String ip;
    private final Date time;

    /**
     * Creates the "Console" player.
     */
    public HammerPlayerInfo() {
        this.uuid = HammerConstants.consoleUUID;
        this.name = "*Console*";
        this.ip = "127.0.0.1";
        this.time = null;
    }

    /**
     * Creates a player.
     * @param uuid The {@link UUID} of the player.
     * @param name The last known name of the player
     * @param ip The IP address of the player.
     */
    public HammerPlayerInfo(UUID uuid, String name, String ip) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.time = null;
    }

    /**
     * Creates a player.
     * @param uuid The {@link UUID} of the player.
     * @param name The last known name of the player
     * @param ip The IP address of the player.
     */
    public HammerPlayerInfo(UUID uuid, String name, String ip, Date time) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.time = time;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public int compareTo(HammerPlayerInfo o) {
        return (time != null && o != null) ? time.compareTo(o.getTime()) : 0;
    }
}
