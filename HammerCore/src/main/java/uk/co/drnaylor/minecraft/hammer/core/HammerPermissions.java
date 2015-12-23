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
package uk.co.drnaylor.minecraft.hammer.core;

/**
 * Contains all the permissions that Hammer uses.
 */
public class HammerPermissions {

    /**
     * Permission to indicate whether the specified player has permission to ban players
     */
    public final static String normalBan = "hammer.ban.normal";

    /**
     * Permission to indicate whether the specified player has permission to ban players permanently
     */
    public final static String permBan = "hammer.ban.perm";

    /**
     * Permission to indicate whether the specified player has permission to ban players from all servers
     */
    public final static String globalBan = "hammer.ban.all";

    /**
     * Permission to indicate whether the specified player has permission to ban players temporarily
     */
    public final static String tempBan = "hammer.ban.temp";

    /**
     * Permission to indicate whether the specified player has permission to unban players
     */
    public final static String normalUnban = "hammer.unban.normal";

    /**
     * Permission to indicate whether the specified player has permission to unban players who
     * have received an all server ban.
     */
    public final static String globalUnban = "hammer.unban.all";

    /**
     * Permission to indicate whether the specified player has permission to unban players with
     * a permanent ban
     */
    public final static String permUnban = "hammer.unban.perm";

    /**
     * Permission to indicate whether the specified player has permission to ban IPs
     */
    public final static String ipBan = "hammer.ipban.norm";

    /**
     * Permission to indicate whether the specified player has permission to ban IPs globally
     */
    public final static String ipBanGlobal = "hammer.ipban.all";

    /**
     * Permission to indicate whether the specified player has permission to unban IPs
     */
    public final static String ipUnban = "hammer.ipunban.norm";

    /**
     * Permission to indicate whether the specified player has permission to unban IPs
     */
    public final static String ipUnbanGlobal = "hammer.ipunban.all";

    /**
     * Permission to indicate whether the specified player has permission to check bans
     */
    public final static String checkBans = "hammer.checkban";

    /**
     * Permission to indicate whether the specified player is always notified of a ban, even the quiet ones.
     */
    public final static String notify = "hammer.notify";

    /**
     * Permission to indicate whether the specified player is exempted from bans.
     */
    public final static String exemptFromBan = "hammer.exempt";

    /**
     * Permission to indicate whether the specified player can import new players from the Mojang Web service.
     */
    public final static String importPlayer = "hammer.importplayer";
}
