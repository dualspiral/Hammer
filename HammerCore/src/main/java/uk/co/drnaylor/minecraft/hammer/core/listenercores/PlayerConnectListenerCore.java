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
package uk.co.drnaylor.minecraft.hammer.core.listenercores;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerUtility;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerIPBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Provides methods and logic for player connection events that is API agnostic
 */
public class PlayerConnectListenerCore {

    private final HammerCore core;

    public PlayerConnectListenerCore(HammerCore core) {
        this.core = core;
    }

    public HammerText handleEvent(WrappedPlayer player, String hostAddress) throws HammerException {
        HammerBan ban = getBan(player.getUUID(), hostAddress);
        InetAddress addr = null;
        try {
            addr = InetAddress.getByName(hostAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (ban == null) {
            if (player.isBanned()) {
                player.unban();
            }

            if (addr != null && core.getWrappedServer().isIPBanned(addr)) {
                core.getWrappedServer().unbanIP(addr);
            }

            return null;
        }

        // Set their ban on the server too - in case Hammer goes down.
        if (ban instanceof HammerPlayerBan && !player.isBanned()) {
            player.ban(core.getWrappedServer().getConsole(), ban.getReason());
        } else if (ban instanceof HammerIPBan && player.isBanned()) {
            player.unban();
        }

        if (ban instanceof HammerIPBan) {
            if (addr != null && !core.getWrappedServer().isIPBanned(addr)) {
                core.getWrappedServer().banIP(addr, ban.getReason());
            }
        }

        return constructBanMessage(ban);
    }

    /**
     * Gets any ban information
     *
     * @param player The {@link UUID} of the player to check
     * @param hostAddress The IP address of the player to check
     * @return The ban, or <code>null</code>
     */
    public HammerBan getBan(UUID player, String hostAddress) throws HammerException {
        // Get the server ID.
        int serverId = core.getConfig().getConfig().getNode("server", "id").getInt();

        try (DatabaseConnection conn = core.getDatabaseConnection()) {
            HammerPlayerBan ban = conn.getBanHandler().getPlayerBanForServer(player, serverId);
            if (ban != null) {
                return ban;
            }

            List<HammerIPBan> ipban = conn.getBanHandler().getIPBanForServer(InetAddress.getByName(hostAddress), serverId);
            if (ipban.isEmpty()) {
                return null;
            }

            return ipban.get(0);
        } catch (Exception ex) {
            throw new HammerException("Connection to the MySQL database failed. Falling back to the Minecraft ban list.", ex);
        }
    }

    public HammerText constructBanMessage(HammerBan ban) {
        String name = ban.getBanningStaffName();
        if (name == null) {
            name = "Unknown";
        }

        HammerTextBuilder htb = new HammerTextBuilder();
        StringBuilder sb = new StringBuilder();

        if (ban instanceof HammerPlayerBan) {
            if (ban.isTempBan()) {
                sb.append("You have been temporarily banned. You may rejoin in ");
                sb.append(HammerUtility.createTimeStringFromOffset(ban.getDateOfUnban().getTime() - new Date().getTime()));
            } else if (((HammerPlayerBan)ban).isPermBan()) {
                sb.append("You have been banned with no right of appeal!");
            } else {
                sb.append("You have been banned!");
            }
        } else if (ban instanceof HammerIPBan) {
            if (ban.isTempBan()) {
                sb.append("You have been temporarily IP banned!");
            } else {
                sb.append("You have been IP banned!");
            }
        }

        sb.append("\n");
        htb.add(sb.toString(), HammerTextColours.RED);
        htb.add("---------\nBanned by: ", HammerTextColours.GRAY);
        htb.add(name + "\n", HammerTextColours.BLUE);
        htb.add("Reason: ", HammerTextColours.GRAY);
        htb.add(ban.getReason(), HammerTextColours.BLUE);
        return htb.build();
    }
}
