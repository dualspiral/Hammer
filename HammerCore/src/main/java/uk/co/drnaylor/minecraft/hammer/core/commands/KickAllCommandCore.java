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

import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.audit.ActionEnum;
import uk.co.drnaylor.minecraft.hammer.core.audit.AuditEntry;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.KickAllFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.KickFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.FlagParser;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.StringParser;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.text.MessageFormat;
import java.util.*;

public class KickAllCommandCore extends CommandCore {

    public KickAllCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.kickall");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        List<ParserEntry> entries = new ArrayList<>();
        entries.add(new ParserEntry("kickall", new FlagParser<>(KickAllFlagEnum.class), true));
        entries.add(new ParserEntry("reason", new StringParser(true), true));
        return entries;
    }

    @Override
    protected boolean requiresDatabase() {
        return false;
    }

    /**
     * Executes the specific routines in this command core with the specified source.
     *
     * @param source    The {@link WrappedCommandSource} that is executing the command.
     * @param arguments The arguments of the command
     * @param conn      If the command requires database access, holds a {@link DatabaseConnection} object. Otherwise, null.
     * @return Whether the command succeeded
     * @throws HammerException Thrown if an exception is thrown in the command core.
     */
    @Override
    protected boolean executeCommand(WrappedCommandSource source, ArgumentMap arguments, DatabaseConnection conn) throws HammerException {
        Optional<List<KickAllFlagEnum>> flagOptional = arguments.<List<KickAllFlagEnum>>getArgument("kickall");
        boolean whitelist = flagOptional.get().contains(KickAllFlagEnum.WHITELIST);
        if (flagOptional.isPresent()) {
            if (whitelist) {
                if (source.hasPermission("hammer.whitelist")) {
                    core.getWrappedServer().setWhitelist(true);
                    sendTemplatedMessage(source, "hammer.kickall.whitelist", false, true);
                } else {
                    sendTemplatedMessage(source, "hammer.kickall.nowhitelist", true, true);
                    return true;
                }
            }
        }

        Optional<String> reasonOptional = arguments.<String>getArgument("reason");
        String reason = reasonOptional.isPresent() ? reasonOptional.get() : messageBundle.getString("hammer.kickall.defaultreason");
        core.getWrappedServer().kickAllPlayers(source, reason);
        sendTemplatedMessage(source, "hammer.kickall", false, true);

        ConfigurationNode cn = core.getConfig().getConfig().getNode("audit");
        if (cn.getNode("database").getBoolean() || cn.getNode("flatfile").getBoolean()) {
            core.getWrappedServer().getScheduler().runAsyncNow(() -> createAuditEntry(source.getUUID(), reason, whitelist));
        }
        return true;
    }

    @Override
    protected String commandName() {
        return "kickall";
    }

    private void createAuditEntry(UUID actor, String reason, boolean isWhitelist) {
        try {
            DatabaseConnection conn = core.getDatabaseConnection();
            String name;
            if (actor.equals(HammerConstants.consoleUUID)) {
                name = String.format("*%s*", messageBundle.getString("hammer.console"));
            } else {
                name = getName(actor, conn);
            }

            String r = MessageFormat.format(messageBundle.getString("hammer.audit.kickall"), name, reason);
            if (isWhitelist) {
                r += " " + messageBundle.getString("hammer.audit.whitelist");
            }

            insertAuditEntry(new AuditEntry(actor, null, core.getConfig().getConfig().getNode("server", "id").getInt(),
                    new Date(), ActionEnum.KICKALL, r), conn);
        } catch (Exception e) {
            core.getWrappedServer().getLogger().warn("Unable to add to audit log.");
            e.printStackTrace();
        }
    }
}
