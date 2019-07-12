package uk.co.drnaylor.minecraft.hammer.sponge.service;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.util.ban.Ban;
import uk.co.drnaylor.minecraft.hammer.core.HammerCore;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Optional;

public class HammerBanService implements BanService {

    private final HammerCore core;

    public HammerBanService(HammerCore core) {
        this.core = core;
    }

    @Override
    public Collection<? extends Ban> getBans() {
        try {
            this.core.getDatabaseConnection().getBanHandler().getIPBanForServer(this.core.getConfig())
        } catch (HammerException e) {
            e.printStackTrace();
            return ImmutableList.of();
        }
    }

    @Override
    public Collection<Ban.Profile> getProfileBans() {
        return null;
    }

    @Override
    public Collection<Ban.Ip> getIpBans() {
        return null;
    }

    @Override
    public Optional<Ban.Profile> getBanFor(GameProfile profile) {
        return Optional.empty();
    }

    @Override
    public Optional<Ban.Ip> getBanFor(InetAddress address) {
        return Optional.empty();
    }

    @Override
    public boolean isBanned(GameProfile profile) {
        return false;
    }

    @Override
    public boolean isBanned(InetAddress address) {
        return false;
    }

    @Override
    public boolean pardon(GameProfile profile) {
        return false;
    }

    @Override
    public boolean pardon(InetAddress address) {
        return false;
    }

    @Override
    public boolean removeBan(Ban ban) {
        return false;
    }

    @Override
    public Optional<? extends Ban> addBan(Ban ban) {
        return Optional.empty();
    }

    @Override
    public boolean hasBan(Ban ban) {
        return false;
    }

}
