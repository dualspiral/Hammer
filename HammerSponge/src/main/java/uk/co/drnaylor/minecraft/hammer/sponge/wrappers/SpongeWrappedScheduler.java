package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import org.spongepowered.api.Game;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedScheduler;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;

public class SpongeWrappedScheduler implements WrappedScheduler {
    private final HammerSponge plugin;
    private final Game game;

    public SpongeWrappedScheduler(HammerSponge plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Override
    public void runSyncNow(Runnable runnable) {
        game.getScheduler().createTaskBuilder().execute(runnable).submit(plugin);
    }

    @Override
    public void runAsyncNow(Runnable runnable) {
        game.getScheduler().createTaskBuilder().async().execute(runnable).submit(plugin);
    }
}
