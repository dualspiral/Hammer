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

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.HammerUtility;
import uk.co.drnaylor.minecraft.hammer.core.commands.parsers.ArgumentMap;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.data.config.BannedPlayers;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreateBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreateBanBuilder;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.runnables.MojangNameRunnable;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunAsync
public class ImportServerBansCommandCore extends CommandCore {

    public ImportServerBansCommandCore(HammerCore core) {
        super(core);

        permissionNodes.add("hammer.importserverbans");
    }

    @Override
    protected List<ParserEntry> createArgumentParserList() {
        return new ArrayList<>();
    }

    @Override
    protected boolean requiresDatabase() {
        return true;
    }

    @Override
    protected boolean executeCommand(WrappedCommandSource source, ArgumentMap arguments, DatabaseConnection conn) throws HammerException {
        // Hi! We're first going to get the banned-players.json file.
        source.sendMessage(new HammerTextBuilder().add("Starting import of banned players...", HammerTextColours.GREEN).build());
        GsonConfigurationLoader gcl = GsonConfigurationLoader.builder().setFile(core.getWrappedServer().getBannedPlayersFile()).build();
        try {
            ConfigurationNode cn = gcl.load();
            List<BannedPlayers> lbp = cn.getList(TypeToken.of(BannedPlayers.class));

            // For each player...
            List<HammerPlayerInfo> lhpi = new ArrayList<>();
            for (BannedPlayers player : lbp) {
                HammerPlayerInfo hpi = conn.getPlayerHandler().getPlayer(player.getUuid());
                if (hpi == null) {
                    // Create the player.
                    lhpi.add(new HammerPlayerInfo(player.getUuid(), player.getName(), "0.0.0.0"));
                }
            }

            if (!lhpi.isEmpty()) {
                conn.getPlayerHandler().updatePlayers(lhpi);
            }

            // Ban them all.
            int serverID = core.getServerId();
            String serverName = core.getConfig().getConfig().getNode("server", "name").getString();
            for (BannedPlayers player : lbp) {
                try {
                    if (!conn.getBanHandler().isBannedFromServer(player.getUuid(), serverID)) {
                        conn.getBanHandler().createPlayerBan(
                                (HammerCreateBan.Player) new HammerCreateBanBuilder(HammerConstants.consoleUUID, serverID, serverName)
                                        .setPlayerToBan(player.getUuid()).setReason(player.getReason())
                                        .setExternalId(conn.getNewExternalID()).build()
                        );
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
            source.sendMessage(new HammerTextBuilder().add("Unable to import banned players. See the console for details.", HammerTextColours.RED).build());
            return false;
        }

        source.sendMessage(new HammerTextBuilder().add("Import of banned players complete!", HammerTextColours.GREEN).build());
        return true;
    }

    @Override
    protected String commandName() {
        return "importserverbans";
    }
}
