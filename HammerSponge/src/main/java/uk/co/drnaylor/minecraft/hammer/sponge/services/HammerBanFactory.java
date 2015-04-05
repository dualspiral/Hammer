package uk.co.drnaylor.minecraft.hammer.sponge.services;

import org.spongepowered.api.entity.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanBuilder;
import org.spongepowered.api.util.ban.BanFactory;

/**
 * Created by daniel on 05/04/15.
 */
public class HammerBanFactory implements BanFactory {
    @Override
    public BanBuilder builder() {
        return null;
    }

    @Override
    public Ban of(User user) {
        return null;
    }

    @Override
    public Ban of(User user, Text.Literal literal) {
        return null;
    }
}
