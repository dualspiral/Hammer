package uk.co.drnaylor.minecraft.hammer.core;

import uk.co.drnaylor.minecraft.hammer.core.interfaces.*;

/**
 * A provider class that allows the plugin to access server implementation
 * specific methods.
 */
public class HammerPluginActionProvider {
    private final IPlayerTranslator playerTranslator;
    private final IConfigurationProvider configProvider;

    public HammerPluginActionProvider(
            IPlayerTranslator playerTranslator, IConfigurationProvider configProvider) {
        this.playerTranslator = playerTranslator;
        this.configProvider = configProvider;
    }

    public IPlayerTranslator getPlayerTranslator() {
        return playerTranslator;
    }

    public IConfigurationProvider getConfigurationProvider() {
        return configProvider;
    }
}
