package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import uk.co.drnaylor.minecraft.hammer.core.interfaces.IConfigurationProvider;

public class SpongeConfigurationProvider implements IConfigurationProvider {
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
