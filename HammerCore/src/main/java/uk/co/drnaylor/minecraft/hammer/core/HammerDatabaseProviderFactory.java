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

import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.config.HammerConfig;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.database.h2.H2FlatFileDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.database.mysql.MySqlDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.database.sqlite.SQLiteDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedServer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

final class HammerDatabaseProviderFactory {
    private HammerDatabaseProviderFactory() {}

    private final static Map<String, Factory> providerFactory = new HashMap<>();

    static {
        providerFactory.put("sqlite", (s, c) -> new SQLiteDatabaseProvider(String.format("%1$s%2$sdata%2$ssqlite.db", s.getDataFolder(), File.separator)));
        providerFactory.put("h2", (s, c) -> new H2FlatFileDatabaseProvider(String.format("%1$s%2$sdata%2$sh2.db", s.getDataFolder(), File.separator)));
        providerFactory.put("mysql", (s, c) -> {
            HammerConfig.MySql mysql = c.getConfig().getMySql();
            return new MySqlDatabaseProvider(
                    mysql.getHost(),
                    mysql.getPort(),
                    mysql.getDatabase(),
                    mysql.getUsername(),
                    mysql.getPassword());
        });
    }

    static IDatabaseProvider createDatabaseProvider(WrappedServer server, HammerConfiguration config) throws HammerException {
        HammerConfig root = config.getConfig();
        String type = root.getEngine();
        Factory f = providerFactory.get(type.toLowerCase());
        if (f == null) {
            throw new HammerException("No database provider for type " + type);
        }

        try {
            return f.create(server, config);
        } catch (Exception e) {
            throw new HammerException("Unable to connect to the database.", e);
        }
    }

    @FunctionalInterface
    private interface Factory {
        IDatabaseProvider create(WrappedServer server, HammerConfiguration configuration) throws ClassNotFoundException, IOException;
    }
}
