package uk.co.drnaylor.minecraft.hammer.core.data;

import java.sql.Time;
import java.util.Date;
import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;

/**
 * Represents data from the Hammer Data store.
 */
public class HammerPlayerInfo implements Comparable<HammerPlayerInfo> {
    private final UUID uuid;
    private final String name;
    private final String ip;
    private final Date time;

    /**
     * Creates the "Console" player.
     */
    public HammerPlayerInfo() {
        this.uuid = HammerConstants.consoleUUID;
        this.name = "*Console*";
        this.ip = "127.0.0.1";
        this.time = null;
    }

    /**
     * Creates a player.
     * @param uuid The {@link UUID} of the player.
     * @param name The last known name of the player
     * @param ip The IP address of the player.
     */
    public HammerPlayerInfo(UUID uuid, String name, String ip) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.time = null;
    }

    /**
     * Creates a player.
     * @param uuid The {@link UUID} of the player.
     * @param name The last known name of the player
     * @param ip The IP address of the player.
     */
    public HammerPlayerInfo(UUID uuid, String name, String ip, Date time) {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;
        this.time = time;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public Date getTime() {
        return time;
    }

    @Override
    public int compareTo(HammerPlayerInfo o) {
        return (time != null && o != null) ? time.compareTo(o.getTime()) : 0;
    }
}
