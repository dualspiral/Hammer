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
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.*;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreateBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

import java.util.*;

public class TempBanCommandCore extends BaseBanCommandCore {

    public TempBanCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.ban.temp");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> entries = new ArrayList<>();
        entries.add(new ParserEntry("flags", new FlagParser<>(BanFlagEnum.class), true));
        entries.add(new ParserEntry("player", new HammerPlayerParser(core), false));
        entries.add(new ParserEntry("time", new TimespanParser(), false));
        entries.add(new ParserEntry("reason", new StringParser(true), true));
        return entries;
    }

    @Override
    protected String commandName() {
        return "tempban";
    }

    @Override
    protected boolean performSpecificActions(HammerCreateBanBuilder builder, ArgumentMap argumentMap) {
        Date until = new Date();
        until = add(until, Calendar.SECOND, argumentMap.<Integer>getArgument("time").get());
        builder.setTemporary(until);
        return true;
    }

    private Date add(Date date, int unit, int span)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(unit, span);
        return cal.getTime();
    }

    @Override
    protected String createReason(ArgumentMap argumentMap, List<String> reasons) {
        Optional<String> reason = argumentMap.getArgument("reason");
        if (!reason.isPresent()) {
            argumentMap.put("reason", "Temporarily Banned.");
        }

        return argumentMap.<String>getArgument("reason").get();
    }

    @Override
    protected BanInfo checkOtherBans(UUID bannedPlayer, DatabaseConnection conn, HammerCreateBanBuilder builder) throws HammerException {
        if (conn.getBanHandler().getPlayerBanForServer(bannedPlayer, core.getConfig().getConfig().getNode("server", "id").getInt()) != null) {
            return new BanInfo(BanStatus.NO_ACTION, null);
        }

        return new BanInfo(BanStatus.CONTINUE, null);
    }
}
