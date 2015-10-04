package uk.co.drnaylor.minecraft.hammer.core.data;

import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;

/**
 * Represents data from the Hammer Data store.
 */
public class HammerPlayerInfo {
    private final UUID uuid;
    private final String name;
    private final String ip;

    /**
     * Creates the "Console" player.
     */
    public HammerPlayerInfo() {
        this.uuid = HammerConstants.consoleUUID;
        this.name = "*Console*";
        this.ip = "127.0.0.1";
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
}
