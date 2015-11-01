package uk.co.drnaylor.minecraft.hammer.bukkit.wrappers;

import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedLogger;

import java.util.logging.Logger;

public class BukkitWrappedLogger implements WrappedLogger {
    private final Logger logger;

    public BukkitWrappedLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void error(String message) {
        logger.severe(message);
    }

    @Override
    public void warn(String message) {
        logger.warning(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }
}
