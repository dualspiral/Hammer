package uk.co.drnaylor.minecraft.hammer.sponge.services;

import org.spongepowered.api.entity.player.User;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.util.ban.Ban;

import java.net.InetAddress;
import java.util.Collection;

public class HammerBanService implements BanService {
    @Override
    public Collection<Ban> getBans() {
        return null;
    }

    @Override
    public Collection<Ban.User> getUserBans() {
        return null;
    }

    @Override
    public Collection<Ban.Ip> getIpBans() {
        return null;
    }

    @Override
    public Collection<Ban.User> getBansFor(User user) {
        return null;
    }

    @Override
    public Collection<Ban.Ip> getBansFor(InetAddress inetAddress) {
        return null;
    }

    @Override
    public boolean isBanned(User user) {
        return false;
    }

    @Override
    public boolean isBanned(InetAddress inetAddress) {
        return false;
    }

    @Override
    public void pardon(User user) {

    }

    @Override
    public void pardon(InetAddress inetAddress) {

    }

    @Override
    public void ban(Ban ban) {

    }

    @Override
    public void pardon(Ban ban) {

    }

    @Override
    public boolean hasBan(Ban ban) {
        return false;
    }
}
