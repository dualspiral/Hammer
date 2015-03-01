package uk.co.drnaylor.minecraft.hammer.core.handlers;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayer;
import uk.co.drnaylor.minecraft.hammer.core.database.IDatabaseGateway;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

public class PlayerHandler {
    private final IDatabaseGateway dg;

    public PlayerHandler(IDatabaseGateway dg) {
        this.dg = dg;
    }

    public void updatePlayer(UUID player, String name, String ip) throws HammerException {
        try {
            dg.updatePlayer(player, name, ip);
        } catch (Exception ex) {
            throw new HammerException("An error occurred updating the player.", ex);
        }
    }

    public void updatePlayers(Collection<HammerPlayer> players) throws HammerException {
        try {
            for (HammerPlayer pl : players) {
                dg.updatePlayer(pl.getUUID(), pl.getName(), pl.getIp());
            }
        } catch (Exception ex) {
            throw new HammerException("An error occurred updating the players.", ex);
        }
    }

    public HammerPlayer getPlayer(UUID uuid) throws HammerException {
        try {
            return dg.getPlayer(uuid);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the player.", ex);
        }
    }

    public List<HammerPlayer> getPlayersByName(String name) throws Exception {
        try {
            return dg.getPlayerFromName(name);
        } catch (Exception ex) {
            throw new HammerException("An error occurred getting the players.", ex);
        }
    }
}
