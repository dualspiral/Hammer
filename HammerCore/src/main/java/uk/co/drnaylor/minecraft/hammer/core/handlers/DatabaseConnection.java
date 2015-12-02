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
package uk.co.drnaylor.minecraft.hammer.core.handlers;

import java.sql.SQLException;

import uk.co.drnaylor.minecraft.hammer.core.audit.AuditEntry;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseProvider;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

public class DatabaseConnection implements AutoCloseable {

    private final IDatabaseProvider provider;
    private IDatabaseGateway gateway;

    public DatabaseConnection(IDatabaseProvider provider) {
        this.provider = provider;
    }

    public void performStartupTasks() throws HammerException {
        openConnectionIfNotOpen();
        try {
            gateway.createTables();
        } catch (SQLException ex) {
            throw new HammerException("Unable to create the tables", ex);
        }
    }

    public AuditHandler getAuditHandler() throws HammerException {
        openConnectionIfNotOpen();
        return new AuditHandler(gateway);
    }

    public BanHandler getBanHandler() throws HammerException {
        openConnectionIfNotOpen();
        return new BanHandler(gateway);
    }

    public PlayerHandler getPlayerHandler() throws HammerException {
        openConnectionIfNotOpen();
        return new PlayerHandler(gateway);
    }

    public ServerHandler getServerHandler() throws HammerException {
        openConnectionIfNotOpen();
        return new ServerHandler(gateway);
    }

    public String getNewExternalID() throws HammerException {
        openConnectionIfNotOpen();
        return new HammerExternalIDGenerator(this).generateExternalId();
    }

    public void startTransaction() throws HammerException {
        try {
            openConnectionIfNotOpen();
            gateway.startTransaction();
        } catch (SQLException ex) {
            throw new HammerException("Could not start transaction", ex);
        }
    }

    public void commitTransaction() throws HammerException {
        try {
            openConnectionIfNotOpen();
            gateway.commitTransaction();
        } catch (SQLException ex) {
            throw new HammerException("Could not commit transaction", ex);
        }
    }

    public void rollbackTransaction() throws HammerException {
        try {
            openConnectionIfNotOpen();
            gateway.rollbackTransaction();
        } catch (SQLException ex) {
            throw new HammerException("Could not rollback transaction", ex);
        }
    }

    @Override
    public void close() throws Exception {
        if (gateway != null) {
            gateway.close();
        }
    }

    private void openConnectionIfNotOpen() throws HammerException {
        try {
            if (gateway == null) {
                gateway = provider.openConnection();
            }
        } catch (SQLException ex) {
            throw new HammerException("Unable to connect to the Database.", ex);
        }
    }

}
