package uk.co.drnaylor.minecraft.hammer.core;

import java.util.UUID;

public class HammerConstants {
    private HammerConstants() { }

    static {
        consoleUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

    public static final UUID consoleUUID;
    public static final String textTag = "[Hammer]";
    public final static String consoleName = "*Console*";
}
