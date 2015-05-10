package uk.co.drnaylor.minecraft.hammer.sponge.coreimpl;

import com.google.common.base.Optional;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import uk.co.drnaylor.minecraft.hammer.core.HammerConstants;
import uk.co.drnaylor.minecraft.hammer.core.interfaces.PlayerPermissionCheckBase;

import java.util.UUID;

public class SpongePlayerPermissionCheck extends PlayerPermissionCheckBase {

    private final Game game;

    public SpongePlayerPermissionCheck(Game game) {
        this.game = game;
    }

    @Override
    public boolean hasPermission(UUID player, String permissionNode) {
        if (player.equals(HammerConstants.consoleUUID)) {
            return true; // Console.
        }

        Optional<Player> pl = game.getServer().getPlayer(player);

        // No player, no permission!
        if (!pl.isPresent()) {
            return false;
        }

        return pl.get().hasPermission(permissionNode);
    }
}
