package uk.co.drnaylor.minecraft.hammer.core.exceptions;

/**
 * General exception for Hammer issues.
 */
public class HammerException extends Exception {
    public HammerException(String message) {
        super(message);
    }

    public HammerException(String message, Exception innerException) {
        super(message, innerException);
    }
}
