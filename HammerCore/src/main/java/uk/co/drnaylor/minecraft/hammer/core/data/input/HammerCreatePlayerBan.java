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

public class HammerCreatePlayerBan {
    private final String externalID;
    private final UUID bannedBy;
    private final boolean isPerm;
    private final String reason;
    private final Integer serverId;
    private final UUID bannedUUID;
    private final Date tempBan;

    HammerCreatePlayerBan(String externalId, UUID bannedBy,
            boolean isPerm, String reason, Integer serverId,
            UUID bannedUUID, Date tempBan) {
        this.externalID = externalId;
        this.bannedBy = bannedBy;
        this.bannedUUID = bannedUUID;
        this.isPerm = isPerm;
        this.reason = reason;
        this.serverId = serverId;
        this.tempBan = tempBan;
    }

    public String getExternalID() {
        return externalID;
    }

    public UUID getBannedUUID() {
        return bannedUUID;
    }

    public UUID getStaffUUID() {
        return bannedBy;
    }

    public boolean isPermanent() {
        return isPerm;
    }
    
    public String getReason() {
        return reason;
    }

    public Integer getServerId() {
        return serverId;
    }

    public Date getTempBanExpiration() {
        return tempBan;
    }
}
