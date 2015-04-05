package uk.co.drnaylor.minecraft.hammer.sponge.services;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanBuilder;
import org.spongepowered.api.util.ban.BanType;
import org.spongepowered.api.util.command.CommandSource;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerBan;
import uk.co.drnaylor.minecraft.hammer.sponge.implementations.HammerUserBan;

import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

public class HammerBanBuilder implements BanBuilder {

    private UUID user = null;
    private String userName = null;
    private UUID banner = HammerConstants.consoleUUID;
    private String bannerName = HammerConstants.consoleName;

    private InetAddress addr = null;
    private BanType type = BanType.USER_BAN;
    private String reason = null;
    private Date startDate = new Date();
    private Date endDate = new Date();

    @Override
    public HammerBanBuilder user(User user) {
        this.user = user.getUniqueId();
        return this;
    }

    @Override
    public HammerBanBuilder address(InetAddress inetAddress) {
        this.addr = inetAddress;
        return this;
    }

    @Override
    public HammerBanBuilder type(BanType banType) {
        this.type = BanType.IP_BAN;
        return this;
    }

    @Override
    public HammerBanBuilder reason(Text.Literal literal) {
        reason = literal.getContent();
        return this;
    }

    @Override
    public HammerBanBuilder startDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("startData cannot be null");
        }

        this.startDate = date;
        return this;
    }

    @Override
    public HammerBanBuilder expirationDate(Date date) {
        this.endDate = date;
        return this;
    }

    @Override
    public HammerBanBuilder source(CommandSource commandSource) {
        if (commandSource instanceof Player) {
            Player player = ((Player)commandSource);
            this.banner = player.getUniqueId();
            this.bannerName = player.getName();
        } else {
            this.banner = HammerConstants.consoleUUID;
            this.bannerName = HammerConstants.consoleName;
        }

        return this;
    }

    @Override
    public HammerUserBan build() {
        throw new UnsupportedOperationException("This has not been implemented yet!");
        // return new HammerUserBan(null);
    }
}
