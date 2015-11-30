package uk.co.drnaylor.minecraft.hammer.core.audit;

import ninja.leaping.configurate.ConfigurationNode;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
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
        ConfigurationNode cn = core.getConfig().getConfig().getNode("audit");
        if (cn.getNode("database").getBoolean()) {
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

        if (cn.getNode("flatfile").getBoolean()) {
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
