/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.drnaylor.minecraft.hammer.core.data.input;

import java.util.Date;
import java.util.UUID;

import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

/**
 * A builder class that helps create a {@link HammerPlayerBan} object safely.
 */
@SuppressWarnings("UnusedReturnValue")
public class HammerCreatePlayerBanBuilder {

    private Date temporary = null;
    private boolean isAll = false;
    private boolean isPerm = false;
    private String reason = null;
    private UUID banned;
    private final UUID staff;
    private final int serverId;
    private final String serverName;
    private String externalId;

    public HammerCreatePlayerBanBuilder(UUID staff, int serverId, String serverName) {
        this.staff = staff;
        this.serverId = serverId;

        // I don't need this, but I'm too lazy to make another object right now!
        this.serverName = serverName;
    }

    public HammerCreatePlayerBanBuilder setTemporary(Date temporary) {
        this.temporary = temporary;
        return this;
    }

    public HammerCreatePlayerBanBuilder clearTemporary() {
        this.temporary = null;
        return this;
    }

    public HammerCreatePlayerBanBuilder setAll(boolean isAll) {
        this.isAll = isAll;
        return this;
    }

    public HammerCreatePlayerBanBuilder setPerm(boolean isPerm) {
        this.isPerm = isPerm;
        return this;
    }

    public HammerCreatePlayerBanBuilder setPlayerToBan(UUID playerToBan) {
        this.banned = playerToBan;
        return this;
    }

    public HammerCreatePlayerBanBuilder setReason(String reason) {
        this.reason = reason;
        return this;
    }

    public HammerCreatePlayerBanBuilder setExternalId(String externalID) {
        this.externalId = externalID;
        return this;
    }

    public boolean isNormalBan() {
        return !(isPerm || isAll || temporary != null);
    }

    public HammerCreatePlayerBan build() throws HammerException {
        if (temporary != null && isPerm) {
            throw new HammerException("A temp ban cannot be set as permanent.");
        }

        if (banned == null) {
            throw new HammerException("A target is needed to ban!");
        }

        if (reason == null) {
            throw new HammerException("A reason is needed to ban!");
        }

        if (externalId == null) {
            throw new HammerException("A new External ID is needed to ban!");
        }

        return new HammerCreatePlayerBan(externalId, staff, isPerm, reason, isAll ? null : serverId, banned, temporary);
    }
}
