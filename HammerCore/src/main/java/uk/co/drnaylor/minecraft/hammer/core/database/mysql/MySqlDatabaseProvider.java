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
package uk.co.drnaylor.minecraft.hammer.core.database.mysql;

import java.sql.DriverManager;
import java.sql.SQLException;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;

/**
 * An adapter to interact with a MySQL database.
 */
public final class MySqlDatabaseProvider implements IDatabaseProvider {

    private final String username;
    private final String password;
    private final String connectionPath;

    /**
     * Constructs this {@link IDatabaseProvider}
     * 
     * @param host The host to connect to
     * @param port The port number to use
     * @param database The name of the database
     * @param username The username to  connect with
     * @param password The password to use when connecting.
     * @throws ClassNotFoundException 
     */
    public MySqlDatabaseProvider(String host, int port, String database, String username, String password) throws ClassNotFoundException {
        this.username = username;
        this.password = password;
        this.connectionPath = String.format("jdbc:mysql://%s:%s/%s", host, port, database);

        Class.forName("com.mysql.jdbc.Driver");
    }

    @Override
    public IDatabaseGateway openConnection() throws SQLException {
        return new MySqlDatabaseGateway(DriverManager.getConnection(connectionPath, username, password));
    }
}
