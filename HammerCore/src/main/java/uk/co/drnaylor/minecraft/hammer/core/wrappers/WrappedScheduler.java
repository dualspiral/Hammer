package uk.co.drnaylor.minecraft.hammer.core.wrappers;

/**
 * Wraps the API specific schedulers
 */
public interface WrappedScheduler {

    /**
     * Runs a task as soon as possible on the main thread.
     *
     * @param runnable The {@link Runnable} to run.
     */
    void runSyncNow(Runnable runnable);

    /**
     * Runs a task as soon as possible on an async thead.
     *
     * @param runnable The {@link Runnable} to run.
     */
    void runAsyncNow(Runnable runnable);
}
