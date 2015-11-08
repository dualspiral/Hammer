package uk.co.drnaylor.minecraft.hammer.bukkit.wrappers;

import org.bukkit.scheduler.BukkitTask;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedSchedulerTask;

public class BukkitWrappedSchedulerTask implements WrappedSchedulerTask {

    private final BukkitTask task;
    private final Runnable runnable;
    private boolean cancelled = false;

    BukkitWrappedSchedulerTask(BukkitTask task, Runnable runnable) {
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
