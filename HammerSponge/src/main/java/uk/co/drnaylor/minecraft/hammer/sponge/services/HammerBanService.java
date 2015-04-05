package uk.co.drnaylor.minecraft.hammer.sponge.services;

import org.spongepowered.api.entity.player.User;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.util.ban.Ban;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;

import java.net.InetAddress;
import java.util.Collection;

public class HammerBanService implements BanService {

    private final HammerSponge sponge;
    private final HammerCore core;

    public HammerBanService(HammerCore core, HammerSponge sponge) {
        this.core = core;
        this.sponge = sponge;
    }

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
        try {
            core.getDatabaseConnection().getBanHandler().getPlayerBanForServer(user.getPlayer().get().getUniqueId(), 1);
        } catch (HammerException e) {
            e.printStackTrace();
        }

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
