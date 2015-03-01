package uk.co.drnaylor.minecraft.hammer.core.interfaces;

public interface IConfigurationProvider {

    String getServerName();

    int getServerId();

    boolean notifyServerOfBans();
}
