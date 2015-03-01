package uk.co.drnaylor.minecraft.hammer.core;

import uk.co.drnaylor.minecraft.hammer.core.interfaces.*;

/**
 * A provider class that allows the plugin to access server implementation
 * specific methods.
 */
public class HammerPluginActionProvider {
    private final IPlayerActions actions;
    private final IPlayerMessageBuilder playerMessageBuilder;
    private final IServerMessageBuilder serverMessageBuilder;
    private final IPlayerToUUIDTranslator playerTranslator;
    private final IPlayerPermissionCheck permissionCheck;
    private final IConfigurationProvider configProvider;

    public HammerPluginActionProvider(
            IPlayerActions actions, IPlayerMessageBuilder playerMessageBuilder, 
            IServerMessageBuilder serverMessageBuilder, IPlayerToUUIDTranslator playerTranslator,
            IPlayerPermissionCheck permissionCheck, IConfigurationProvider configProvider) {
        this.actions = actions;
        this.playerMessageBuilder = playerMessageBuilder;
        this.serverMessageBuilder = serverMessageBuilder;
        this.playerTranslator = playerTranslator;
        this.permissionCheck = permissionCheck;
        this.configProvider = configProvider;
    }

    public IPlayerActions getPlayerActions() {
        return actions;
    }

    public IPlayerMessageBuilder getPlayerMessageBuilder() {
        return playerMessageBuilder;
    }

    public IServerMessageBuilder getServerMessageBuilder() {
        return serverMessageBuilder;
    }

    public IPlayerToUUIDTranslator getPlayerTranslator() {
        return playerTranslator;
    }

    public IPlayerPermissionCheck getPermissionCheck() {
        return permissionCheck;
    }

    public IConfigurationProvider getConfigurationProvider() {
        return configProvider;
    }
}
