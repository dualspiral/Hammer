package uk.co.drnaylor.minecraft.hammer.core.runnables;

import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class BanCheckRunnable implements Runnable {

    private final HammerCore core;

    public BanCheckRunnable(HammerCore core) {
        this.core = core;
    }

    @Override
    public void run() {
        // Get the server bans for any players currently online.
        Set<UUID> players = core.getWrappedServer().getOnlinePlayers().stream().map(WrappedPlayer::getUUID).collect(Collectors.toSet());


    }
}
