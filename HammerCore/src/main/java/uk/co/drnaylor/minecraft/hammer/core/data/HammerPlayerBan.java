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
package uk.co.drnaylor.minecraft.hammer.core.data;

import java.util.Date;
import java.util.UUID;

public class HammerPlayerBan extends HammerBan {

    private final String bannedName;
    private final UUID bannedUUID;
    private final boolean permanent;
    private final String externalId;

    public HammerPlayerBan(String bannedName, UUID bannedUUID, boolean isPermanent, 
            UUID banningStaffUUID, String banningStaffName, String reason, 
            Date dateOfBan, Date dateOfUnban, Integer serverId, String serverName, String externalId) {
        super(banningStaffUUID, banningStaffName, reason, dateOfBan, dateOfUnban, serverId, serverName);
        this.bannedName = bannedName;
        this.bannedUUID = bannedUUID;
        this.permanent = isPermanent;
        this.externalId = externalId;
    }

    public String getBannedLastKnownName() {
        return bannedName;
    }

    public UUID getBannedUUID() {
        return bannedUUID;
    }

    public boolean isPermBan() {
        return permanent;
    }

    public String getExternalId() {
        return externalId;
    }
}
