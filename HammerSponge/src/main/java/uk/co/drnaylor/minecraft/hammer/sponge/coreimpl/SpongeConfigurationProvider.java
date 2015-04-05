package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.service.config.DefaultConfig;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IConfigurationProvider;

import java.io.File;

public class SpongeConfigurationProvider implements IConfigurationProvider {
    @Inject
    @DefaultConfig(sharedRoot = true)
    private File configFile;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode> configurationManager;

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerId() {
        return 0;
    }

    @Override
    public boolean notifyServerOfBans() {
        return false;
    }
}
