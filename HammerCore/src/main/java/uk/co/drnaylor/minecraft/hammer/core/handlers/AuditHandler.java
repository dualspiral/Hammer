package uk.co.drnaylor.minecraft.hammer.core.handlers;

import uk.co.drnaylor.minecraft.hammer.core.audit.AuditEntry;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

public class AuditHandler {

    private final IDatabaseGateway dg;

    AuditHandler(IDatabaseGateway dg) {
        this.dg = dg;
    }

    public void insertAuditAction(AuditEntry entry) throws HammerException {
        try {
            dg.insertAuditEntry(entry);
        } catch (Exception ex) {
            throw new HammerException("An error occurred setting the player ban.", ex);
        }
    }
}
