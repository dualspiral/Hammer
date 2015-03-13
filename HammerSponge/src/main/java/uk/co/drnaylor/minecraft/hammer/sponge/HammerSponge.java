package uk.co.drnaylor.minecraft.hammer.sponge;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.util.event.Subscribe;

/**
 * Sponge plugin entrypoint
 */
@Plugin(id = "hammersponge", name = "Hammer for Sponge", version = "1.0")
public class HammerSponge {

    @Inject
    private Logger logger;

    /**
     * Runs when the server has started.
     *
     * @param event The event
     */
    @Subscribe
    public void onServerStart(ServerStartedEvent event) {

    }
}
