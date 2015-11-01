package uk.co.drnaylor.minecraft.hammer.core.wrappers;

/**
 * Contains methods to expose the implementation loggers.
 */
public interface WrappedLogger {

    /**
     * Sends an error message to the logger.
     *
     * @param message The message
     */
    void error(String message);

    /**
     * Sends a warning message to the logger.
     *
     * @param message The message
     */
    void warn(String message);

    /**
     * Sends an info message to the logger.
     *
     * @param message The message
     */
    void info(String message);
}
