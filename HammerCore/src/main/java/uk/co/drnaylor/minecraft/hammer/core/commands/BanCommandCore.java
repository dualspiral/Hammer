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

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.BanFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.FlagParser;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.HammerPlayerParser;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.StringParser;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreateBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    protected boolean performSpecificActions(HammerCreateBanBuilder builder, ArgumentMap argumentMap) {
        // Nothing
        return true;
    }

    @Override
    protected BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, HammerCreateBanBuilder proposedBan) throws HammerException {
        // Check if they are already banned.
        List<HammerPlayerBan> bans = conn.getBanHandler().getPlayerBans(bannedPlayer);
        List<String> reasons = bans.stream().filter(b -> !b.isTempBan()).map(HammerBan::getReason).collect(Collectors.toList());

        // If we are globalling banning...
        if (proposedBan.isGlobal()) {
            Stream<HammerPlayerBan> sp = bans.stream().filter(b -> b.getServerId() == null);
            if (sp.count() != 0) {
                // We have a global already.
                if (sp.anyMatch(HammerPlayerBan::isPermBan) && !proposedBan.isPerm()) {
                    // It's a permanent ban, upgrade.
                    proposedBan.setPerm(true);
                    return new BanInfo(BanStatus.TO_PERM, reasons);
                }

                // It's not perm.
                return new BanInfo(BanStatus.NO_ACTION, null);
            }

            // No global ban exists, but are we going permanent?
            boolean goingToPerm = !proposedBan.isPerm() && bans.stream().anyMatch(HammerPlayerBan::isPermBan);
            if (goingToPerm) {
                proposedBan.setPerm(true);
            }

            // If it's going global, then unban all current.
            conn.getBanHandler().unbanFromAllServers(bannedPlayer);
            return new BanInfo(goingToPerm ? BanStatus.TO_PERM : BanStatus.CONTINUE, reasons);
        }

        Collection<HammerPlayerBan> s = bans.stream().filter(b -> b.getServerId() == null || Objects.equals(b.getServerId(), core.getConfig().getConfig().getNode("server", "id").getInt()))
                .collect(Collectors.toList());
        if (s.isEmpty()) {
            // Nothing to change, or we're upgrading to perm.
            return new BanInfo(BanStatus.CONTINUE, reasons);
        } else if (proposedBan.isPerm() && s.stream().noneMatch(HammerPlayerBan::isPermBan)) {

            // Make it global if we already have a global ban.
            if (s.stream().anyMatch(b -> b.getServerId() == null)) {
                return new BanInfo(BanStatus.TO_GLOBAL, reasons);
            }

            // We're upgrading to perm.
            conn.getBanHandler().unbanFromServer(bannedPlayer, core.getConfig().getConfig().getNode("server", "id").getInt());
            return new BanInfo(BanStatus.CONTINUE, reasons);
        }

        // No action is needed.
        return new BanInfo(BanStatus.NO_ACTION, null);
    }
}
