package uk.co.drnaylor.minecraft.hammer.core;

import java.util.UUID;

/**
 * Contains all the permissions that Hammer uses.
 */
public class HammerPermissions {

    /**
     * Permission to indicate whether the specified player has permission to ban players
     */
    public final static String normalBan = "hammer.ban.normal";

    /**
     * Permission to indicate whether the specified player has permission to ban players permanently
     */
    public final static String permBan = "hammer.ban.perm";

    /**
     * Permission to indicate whether the specified player has permission to ban players from all servers
     */
    public final static String globalBan = "hammer.ban.all";

    /**
     * Permission to indicate whether the specified player has permission to ban players temporarily
     */
    public final static String tempBan = "hammer.ban.temp";

    /**
     * Permission to indicate whether the specified player has permission to unban players
     */
    public final static String normalUnban = "hammer.unban.normal";

    /**
     * Permission to indicate whether the specified player has permission to unban players who
     * have received an all server ban.
     */
    public final static String globalUnban = "hammer.unban.all";

    /**
     * Permission to indicate whether the specified player has permission to unban players with
     * a permanent ban
     */
    public final static String permUnban = "hammer.unban.perm";

    /**
     * Permission to indicate whether the specified player has permission to ban IPs
     */
    public final static String ipBan = "hammer.ipban";

    /**
     * Permission to indicate whether the specified player has permission to unban IPs
     */
    public final static String ipUnban = "hammer.ipunban";

    /**
     * Permission to indicate whether the specified player has permission to check bans
     */
    public final static String checkBans = "hammer.checkban";

    /**
     * Permission to indicate whether the specified player is always notified of a ban, even the quiet ones.
     */
    public final static String notify = "hammer.notify";

    /**
     * Permission to indicate whether the specified player is exempted from bans.
     */
    public final static String exemptFromBan = "hammer.exempt";
}
