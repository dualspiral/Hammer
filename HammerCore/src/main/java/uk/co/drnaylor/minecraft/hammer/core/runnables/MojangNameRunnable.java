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
package uk.co.drnaylor.minecraft.hammer.core.runnables;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextColours;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gets the UUID for a name via the Mojang API.
 */
public class MojangNameRunnable implements Runnable {

    private final WrappedCommandSource source;
    private final HammerCore core;
    private final String playerName;

    MojangNameRunnable(WrappedCommandSource source, HammerCore core) {
        this(source, core, null);
    }

    public MojangNameRunnable(WrappedCommandSource source, HammerCore core, String playerName) {
        this.source = source;
        this.core = core;
        this.playerName = playerName;
    }

    @Override
    public void run() {
        Preconditions.checkNotNull(playerName);

        try (DatabaseConnection conn = core.getDatabaseConnection()) {
            if (addPlayer(playerName, conn)) {
                // And that's all there is to it! We just have to tell the user.
                added();
            } else {
                noAdditions();
            }
        } catch (Exception e) {
            e.printStackTrace();
            noAdditions();
        }
    }

    public boolean addPlayer(String pl, DatabaseConnection conn) {
        String json = getJsonResponse(pl);
        if (json == null) {
            return false;
        }

        Gson gson = new Gson();
        PlayerData pd = gson.fromJson(json, PlayerData.class);

        // Got the data, now shove it into the system.
        try {
            conn.getPlayerHandler().updatePlayer(pd.getID(), pd.name, "0.0.0.0");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void noAdditions() {
        HammerText ht1 = new HammerTextBuilder().add(HammerConstants.textTag, HammerTextColours.RED).add(" The player ", HammerTextColours.RED)
                .add(playerName, HammerTextColours.YELLOW).add(" could not be added to Hammer - most likely because they do not exist.", HammerTextColours.RED).build();
        core.getWrappedServer().getScheduler().runSyncNow(new MessageSenderRunnable(source, ht1));
    }

    private void added() {
        HammerText ht1 = new HammerTextBuilder().add(HammerConstants.textTag, HammerTextColours.GREEN).add(" The player ", HammerTextColours.GREEN)
                .add(playerName, HammerTextColours.YELLOW).add(" was added to Hammer successfully.", HammerTextColours.GREEN).build();
        core.getWrappedServer().getScheduler().runSyncNow(new MessageSenderRunnable(source, ht1));
    }

    private String getJsonResponse(String plName) {
        String target = String.format("https://api.mojang.com/users/profiles/minecraft/%s", plName);
        String r = null;

        // I have no idea what I would do without Stack Overflow.
        // http://stackoverflow.com/a/1359700
        HttpURLConnection connection = null;
        try {
            URL url = new URL(target);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            // Send request
            connection.connect();

            // And let's get that response back!
            if (connection.getResponseCode() != 200)
            {
                // We got nothin'
                return null;
            }

            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder();
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r').append('\n');
            }
            rd.close();

            r = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }

        return r;
    }

    private static class PlayerData
    {
        private static final Pattern uuidRegex = Pattern.compile("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})");

        public String id;
        public String name;

        UUID getID() {
            Matcher m = uuidRegex.matcher(id);
            if (m.matches()) {
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= 5; i++) {
                    if (i > 1) {
                        sb.append("-");
                    }

                    sb.append(m.group(i));
                }

                return UUID.fromString(sb.toString());
            }

            return UUID.fromString(id);
        }
    }
}
