package uk.co.drnaylor.minecraft.hammer.core;

import uk.co.drnaylor.minecraft.hammer.core.interfaces.*;

/**
 * A provider class that allows the plugin to access server implementation
 * specific methods.
 */
public class HammerPluginActionProvider {
    private final IPlayerActions actions;
    private final IPlayerTranslator playerTranslator;
    private final IPlayerPermissionCheck permissionCheck;
    private final IConfigurationProvider configProvider;
    private final IMessageSender messageSender;

    public HammerPluginActionProvider(
            IPlayerActions actions, IMessageSender messageSender, IPlayerTranslator playerTranslator,
            IPlayerPermissionCheck permissionCheck, IConfigurationProvider configProvider) {
        this.actions = actions;
        this.messageSender = messageSender;
        this.playerTranslator = playerTranslator;
        this.permissionCheck = permissionCheck;
        this.configProvider = configProvider;
    }

    public IPlayerActions getPlayerActions() {
        return actions;
    }

    public IMessageSender getMessageSender() { return messageSender; }

    public IPlayerTranslator getPlayerTranslator() {
        return playerTranslator;
    }

    public IPlayerPermissionCheck getPermissionCheck() {
        return permissionCheck;
    }

    public IConfigurationProvider getConfigurationProvider() {
        return configProvider;
    }
}
