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

import com.google.common.collect.Lists;
import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerPermissions;
import uk.co.drnaylor.minecraft.hammer.core.audit.ActionEnum;
import uk.co.drnaylor.minecraft.hammer.core.audit.AuditEntry;
import uk.co.drnaylor.minecraft.hammer.core.commands.enums.UnbanIPFlagEnum;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.FlagParser;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.IP4Parser;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerIPBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@RunAsync
public class UnbanIPCommandCore extends CommandCore {

    public UnbanIPCommandCore(HammerCore core) {
        super(core);
        permissionNodes.add("hammer.ipunban.norm");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        return Lists.newArrayList(
            new ParserEntry("flags", new FlagParser<>(UnbanIPFlagEnum.class), true),
            new ParserEntry("ip", new IP4Parser(), false)
        );
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
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
        List<UnbanIPFlagEnum> flags = arguments.<List<UnbanIPFlagEnum>>getArgument("flags").orElse(Lists.newArrayList());
        if (flags.contains(UnbanIPFlagEnum.ALL) && !source.hasPermission(HammerPermissions.ipUnbanGlobal)) {
            sendTemplatedMessage(source, "hammer.player.noperms", true, true);
            return true;
        }

        int serverId = core.getConfig().getConfig().getNode("server", "id").getInt();

        InetAddress ip = arguments.<InetAddress>getArgument("ip").get();
        List<HammerIPBan> listipbans = conn.getBanHandler().getIPBanForServer(ip, serverId);
        if (listipbans.isEmpty()) {
            sendTemplatedMessage(source, "hammer.ipban.noban", true, true, ip.getHostAddress());
            return true;
        }

        // We have an IP ban. Are any of them global?
        if (!flags.contains(UnbanIPFlagEnum.ALL) && listipbans.stream().anyMatch(b -> b.getServerId() == null)) {
            sendTemplatedMessage(source, "hammer.ipban.allservers", true, true);
            return true;
        }

        HammerTextBuilder htb = new HammerTextBuilder().add("[Hammer] ", HammerTextColours.GREEN);
        if (flags.contains(UnbanIPFlagEnum.ALL)) {
            conn.getBanHandler().unbanIpFromAllServers(ip);
            htb.add(MessageFormat.format(messageBundle.getString("hammer.ipban.unban"), ip.getHostAddress(),
                    messageBundle.containsKey("hammer.allservers"), HammerTextColours.GREEN));
        } else {
            conn.getBanHandler().unbanIpFromServer(ip, serverId);
            htb.add(MessageFormat.format(messageBundle.getString("hammer.ipban.unban"), ip.getHostAddress(),
                    messageBundle.containsKey("hammer.thisserver"), HammerTextColours.GREEN));
        }

        conn.commitTransaction();
        core.getWrappedServer().unbanIP(ip);
        core.getWrappedServer().sendMessageToPermissionGroup(htb.build(), HammerPermissions.notify);
        ConfigurationNode cn = core.getConfig().getConfig().getNode("audit");
        if (cn.getNode("database").getBoolean() || cn.getNode("flatfile").getBoolean()) {
            createAuditLog(source, ip, conn);
        }

        return true;
    }

    @Override
    protected String commandName() {
        return "ipunban";
    }

    private void createAuditLog(WrappedCommandSource source, InetAddress ip, DatabaseConnection conn) {
        int id = core.getConfig().getConfig().getNode("server", "id").getInt();

        try {
            String ipaddr = ip.getHostAddress();
            String name;
            if (source.getUUID().equals(HammerConstants.consoleUUID)) {
                name = String.format("*%s*", messageBundle.getString("hammer.console"));
            } else {
                name = getName(source.getUUID(), conn);
            }

            AuditEntry ae = new AuditEntry(source.getUUID(), null, id, new Date(), ActionEnum.UNBAN,
                    MessageFormat.format(messageBundle.getString("hammer.audit.ipunban"), ipaddr, name));

            insertAuditEntry(ae, conn);
        } catch (HammerException e) {
            core.getWrappedServer().getLogger().warn("Could not add audit entry.");
            e.printStackTrace();
        }
    }
}
