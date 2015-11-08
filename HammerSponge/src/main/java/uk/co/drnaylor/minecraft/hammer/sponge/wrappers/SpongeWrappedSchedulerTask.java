package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import org.spongepowered.api.service.scheduler.Task;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedSchedulerTask;

/**
 *
 */
public class SpongeWrappedSchedulerTask implements WrappedSchedulerTask {

    private final Task task;
    private final Runnable runnable;
    private boolean cancelled = false;

    SpongeWrappedSchedulerTask(Task task, Runnable runnable) {
        this.task = task;
        this.runnable = runnable;
    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void cancelTask() {
        task.cancel();
        cancelled = true;
    }
}
