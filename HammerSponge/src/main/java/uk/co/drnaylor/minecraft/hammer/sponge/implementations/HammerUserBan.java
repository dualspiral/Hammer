package uk.co.drnaylor.minecraft.hammer.sponge.implementations;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanType;
import org.spongepowered.api.util.command.CommandSource;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;

import javax.swing.text.html.Option;
import java.awt.*;
import java.util.Date;

public class HammerUserBan implements Ban.User {

    @Inject
    private Game game;

    private final HammerPlayerBan ban;

    public HammerUserBan(HammerPlayerBan ban) {
        this.ban = ban;
    }

    @Override
    public User getUser() {
        // TODO: Get the user
        return null;
    }

    @Override
    public BanType getType() {
        return BanType.USER_BAN;
    }

    @Override
    public Text.Literal getReason() {
        return Texts.of(ban.getReason()).builder().color(TextColors.RED).build();
    }

    @Override
    public Date getStartDate() {
        return ban.getDateOfBan();
    }

    @Override
    public Optional<CommandSource> getSource() {
        // TODO: Work this out.
        if (ban.getBannedUUID().equals(HammerConstants.consoleUUID)) {
            return Optional.absent();
        }

        return Optional.absent();
    }

    @Override
    public Optional<Date> getExpirationDate() {
        if (ban.isTempBan()) {
            return Optional.of(ban.getDateOfBan());
        }

        return Optional.absent();
    }

    @Override
    public boolean isIndefinite() {
        return !ban.isTempBan();
    }
}
