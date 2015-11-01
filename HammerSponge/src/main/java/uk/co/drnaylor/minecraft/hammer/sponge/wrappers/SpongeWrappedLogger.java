package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import org.slf4j.Logger;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedLogger;

public class SpongeWrappedLogger implements WrappedLogger {

    private final Logger logger;

    public SpongeWrappedLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }
}
