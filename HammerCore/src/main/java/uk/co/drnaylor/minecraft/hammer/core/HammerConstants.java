package uk.co.drnaylor.minecraft.hammer.core;

import java.util.UUID;

public class HammerConstants {
    private HammerConstants() { }

    static {
        consoleUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

    /**
     * The pseudo-UUID for the console
     */
    public static final UUID consoleUUID;

    /**
     * The Hammer prefix to use in messages
     */
    public static final String textTag = "[Hammer]";

    /**
     * The name of the console
     */
    public final static String consoleName = "*Console*";
}
