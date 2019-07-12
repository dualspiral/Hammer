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
package uk.co.drnaylor.minecraft.hammer.core.audit;

import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.config.HammerConfig;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;

public final class AuditHelper {

    private final HammerCore core;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public AuditHelper(HammerCore core) {
        this.core = core;
    }

    /**
     * Inserts an {@link AuditEntry} into Hammer.
     *
     * <p>
     *     Care should be taken to do this off the main thread.
     * </p>
     *
     * @param ae The {@link AuditEntry}
     * @param conn The {@link DatabaseConnection} to use, if one exists. Pass <code>null</code> to get a new connection.
     * @throws HammerException Thrown if an error logging occurs. As this is auditing, this can be treated as non-fatal.
     */
    public void insertAuditEntry(AuditEntry ae, DatabaseConnection conn) throws HammerException {
        HammerConfig.Audit cn = core.getConfig().getConfig().getAudit();
        if (cn.isDatabase()) {
            if (conn == null) {
                try (DatabaseConnection c = core.getDatabaseConnection()) {
                    c.getAuditHandler().insertAuditAction(ae);
                } catch (Exception e) {
                    throw new HammerException("Failed to set up a connection", e);
                }
            } else {
                conn.getAuditHandler().insertAuditAction(ae);
            }
        }

        if (cn.isFlatfile()) {
            addFlatFileAuditEntry(ae);
        }
    }

    private synchronized void addFlatFileAuditEntry(AuditEntry entry) {
        try {
            String filename = core.getWrappedServer().getLogFolder() + File.separator + dateFormat.format(entry.getDate()) + ".log";
            Path file = Paths.get(filename);
            if (!Files.exists(file)) {
                Files.createFile(file);
            }

            Files.write(file, createAuditEntryText(entry), StandardOpenOption.APPEND);
        } catch (Exception e) {
            core.getWrappedServer().getLogger().warn("Could not log to audit file.");
            e.printStackTrace();
        }
    }

    private byte[] createAuditEntryText(AuditEntry entry) {
        StringBuilder builder = new StringBuilder("[").append(dateTimeFormat.format(entry.getDate())).append("]");
        builder.append("[").append(entry.getActionType().name()).append("]");
        builder.append(" ").append(entry.getEvent());
        return builder.toString().getBytes();
    }
}
