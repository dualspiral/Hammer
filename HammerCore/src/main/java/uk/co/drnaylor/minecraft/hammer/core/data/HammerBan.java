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

public abstract class HammerBan {

    private final UUID banningStaffUUID;
    private final String banningStaffName;
    private final String reason;
    private final Date dateOfBan;
    private final Date dateOfUnban;
    
    /* Server Id can be null */
    private final Integer serverId;
    private final String serverName;

    HammerBan(UUID banningStaffUUID, String banningStaffName, String reason, Date dateOfBan, Date dateOfUnban, Integer serverId, String serverName) {
        this.banningStaffUUID = banningStaffUUID;
        this.banningStaffName = banningStaffName;
        this.reason = reason;
        this.dateOfBan = dateOfBan;
        this.dateOfUnban = dateOfUnban;
        this.serverId = serverId;
        this.serverName = serverName;
    }

    public String getReason() {
        return reason;
    }

    public UUID getBanningStaff() {
        return banningStaffUUID;
    }

    public String getBanningStaffName() {
        return banningStaffName;
    }

    public Date getDateOfBan() {
        return dateOfBan;
    }

    public Date getDateOfUnban() {
        return dateOfUnban;
    }

    public boolean isTempBan() {
        return this.dateOfUnban != null;
    }

    public Integer getServerId() {
        return serverId;
    }

    public String getServerName() { return serverName; }
}
