package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import org.spongepowered.api.Game;
import org.spongepowered.api.service.scheduler.Task;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedScheduler;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedSchedulerTask;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;

import java.util.concurrent.TimeUnit;

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

    @Override
    public WrappedSchedulerTask createAsyncRecurringTask(Runnable runnable, int seconds) {
        Task t = game.getScheduler().createTaskBuilder().async().interval(seconds, TimeUnit.SECONDS)
                .delay(seconds, TimeUnit.SECONDS).execute(runnable).submit(plugin);

        return new SpongeWrappedSchedulerTask(t, runnable);
    }
}
