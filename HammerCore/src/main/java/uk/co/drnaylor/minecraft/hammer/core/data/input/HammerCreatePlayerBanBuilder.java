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

    public boolean isPerm() {
        return isPerm;
    }

    public boolean isGlobal() {
        return isAll;
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
