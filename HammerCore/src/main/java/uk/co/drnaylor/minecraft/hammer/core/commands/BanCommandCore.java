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

import java.util.*;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.BanFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.*;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;

/**
 * Provides the core Ban Command, based on the player and arguments sent down.
 */
public class BanCommandCore extends BaseBanCommandCore {

    public BanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.ban.normal");
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
        return "ban";
    }

    @Override
    protected boolean performSpecificActions(HammerCreatePlayerBanBuilder builder, ArgumentMap argumentMap) {
        // Nothing
        return true;
    }

    @Override
    protected BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, boolean isGlobal) throws HammerException {
        // Check if they are already banned.
        List<HammerPlayerBan> bans = conn.getBanHandler().getPlayerBans(bannedPlayer);
        if (isGlobal) {
            List<String> reasons = new ArrayList<>();
            boolean isPerm = false;
            for (HammerPlayerBan ban : bans) {
                if (!ban.isTempBan()) {
                    reasons.add(ban.getReason());
                }

                if (ban.getServerId() == null) {
                    return new BanInfo(BanStatus.NO_ACTION, null);
                }

                if (ban.isPermBan()) {
                    isPerm = true;
                }
            }

            // If it's going global, then unban all current.
            conn.getBanHandler().unbanFromAllServers(bannedPlayer);
            return new BanInfo(isPerm ? BanStatus.TO_PERM : BanStatus.CONTINUE, reasons);
        }

        for (HammerPlayerBan ban : bans) {
            Integer serverId = ban.getServerId();
            if (serverId == null || Objects.equals(serverId, core.getConfig().getConfig().getNode("server", "id").getInt())) {
                // Banned. No further action.
                return new BanInfo(BanStatus.NO_ACTION, null);
            }
        }

        return new BanInfo(BanStatus.CONTINUE, null);
    }
}
