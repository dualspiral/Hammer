package uk.co.drnaylor.minecraft.hammer.spigot.coreimpl;

import uk.co.drnaylor.minecraft.hammer.spigot.HammerSpigot;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.IConfigurationProvider;

public class BukkitConfigurationProvider implements IConfigurationProvider {

    private final HammerSpigot plugin;

    public BukkitConfigurationProvider(HammerSpigot plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getServerName() {
        return plugin.getConfig().getString("server.name");
    }

    @Override
    public int getServerId() {
        return plugin.getConfig().getInt("server.id");
    }

    @Override
    public boolean notifyServerOfBans() {
        return plugin.getConfig().getBoolean("notifyAllOnBan");
    }
}
