package uk.co.drnaylor.minecraft.hammer.core.listenercores;

import uk.co.drnaylor.minecraft.hammer.core.runnables.HammerPlayerUpdateRunnable;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;

public class PlayerJoinListenerCore {

    private final HammerPlayerUpdateRunnable runnable;

    public PlayerJoinListenerCore(HammerPlayerUpdateRunnable runnable) {
        this.runnable = runnable;
    }

    public void handleEvent(WrappedPlayer pl) {
        runnable.addPlayer(pl);
    }

    public HammerPlayerUpdateRunnable getRunnable() {
        return runnable;
    }
}
