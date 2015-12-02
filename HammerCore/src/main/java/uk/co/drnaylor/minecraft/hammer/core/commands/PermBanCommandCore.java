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
package uk.co.drnaylor.minecraft.hammer.core.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.BanFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.FlagParser;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.HammerPlayerParser;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.StringParser;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

public class PermBanCommandCore extends BaseBanCommandCore {

    public PermBanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.ban.perm");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> entries = new ArrayList<>();
        entries.add(new ParserEntry("flags", new FlagParser<>(BanFlagEnum.class), true));
        entries.add(new ParserEntry("player", new HammerPlayerParser(core), false));
        entries.add(new ParserEntry("reason", new StringParser(true), false));
        return entries;
    }

    @Override
    protected String commandName() {
        return "permban";
    }

    @Override
    protected boolean performSpecificActions(HammerCreatePlayerBanBuilder builder, ArgumentMap argumentMap) {
        builder.setPerm(true);
        return true;
    }

    @Override
    protected BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, boolean isGlobal) throws HammerException {
        // Check if they are already banned.
        List<HammerPlayerBan> bans = conn.getBanHandler().getPlayerBans(bannedPlayer);
        if (isGlobal) {
            List<String> reasons = new ArrayList<>();
            for (HammerPlayerBan ban : bans) {
                if (!ban.isTempBan()) {
                    reasons.add(ban.getReason());
                }

                if (ban.getServerId() == null && ban.isPermBan()) {
                    return new BanInfo(BanStatus.NO_ACTION, null);
                }
            }

            // If it's going global, then unban all current.
            conn.getBanHandler().unbanFromAllServers(bannedPlayer);
            return new BanInfo(BanStatus.CONTINUE, reasons);
        }

        ConfigurationNode cn = core.getConfig().getConfig();
        int currentServerId = cn.getNode("server", "id").getInt();
        for (HammerPlayerBan ban : bans) {
            Integer serverId = ban.getServerId();
            if (serverId == null) {
                if (ban.isPermBan()) {
                    // Global perm. No action.
                    return new BanInfo(BanStatus.NO_ACTION, null);
                }

                List<String> reason = new ArrayList<>();
                reason.add(ban.getReason());
                return new BanInfo(BanStatus.TO_GLOBAL, reason);
            } else if (serverId == currentServerId && ban.isPermBan()) {
                return new BanInfo(BanStatus.NO_ACTION, null);
            }
        }

        conn.getBanHandler().unbanFromServer(bannedPlayer, currentServerId);
        return new BanInfo(BanStatus.CONTINUE, null);
    }
}
