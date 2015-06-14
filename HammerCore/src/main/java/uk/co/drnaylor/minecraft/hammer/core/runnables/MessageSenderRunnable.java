package uk.co.drnaylor.minecraft.hammer.core.runnables;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;

/**
 * Sends a message to a player.
 */
public class MessageSenderRunnable implements Runnable {

    private final WrappedCommandSource source;
    private final HammerText[] message;

    public MessageSenderRunnable(WrappedCommandSource source, HammerText... message) {
        this.source = source;
        this.message = message;
    }

    @Override
    public void run() {
        for (HammerText m : message) {
            source.sendMessage(m);
        }
    }
}
