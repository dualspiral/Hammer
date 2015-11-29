package uk.co.drnaylor.minecraft.hammer.core.audit;

import java.util.Date;
import java.util.UUID;

public class AuditEntry {

    private final UUID actor;
    private final UUID target;
    private final int serverId;
    private final Date date;
    private final ActionEnum actionType;
    private final String event;

    public AuditEntry(UUID actor, UUID target, int serverId, Date date, ActionEnum actionType, String event) {
        this.actor = actor;
        this.target = target;
        this.serverId = serverId;
        this.date = date;
        this.actionType = actionType;
        this.event = event;
    }

    public UUID getActor() {
        return actor;
    }

    public UUID getTarget() {
        return target;
    }

    public int getServerId() {
        return serverId;
    }

    public Date getDate() {
        return date;
    }

    public ActionEnum getActionType() {
        return actionType;
    }

    public String getEvent() {
        return event;
    }
}
