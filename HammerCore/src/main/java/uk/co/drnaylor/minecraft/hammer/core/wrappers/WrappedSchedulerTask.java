package uk.co.drnaylor.minecraft.hammer.core.wrappers;

/**
 * Represents a task
 */
public interface WrappedSchedulerTask {

    /**
     * Gets the runnable from the task.
     *
     * @return The runnable to get.
     */
    Runnable getRunnable();

    /**
     * Returns whether the task has been cancelled or not.
     *
     * @return <code>true</code> if the task has been cancelled.
     */
    boolean isCancelled();

    /**
     * Cancels the task, if it has not already been cancelled.
     */
    void cancelTask();
}
