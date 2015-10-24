package uk.co.drnaylor.minecraft.hammer.core.database;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.data.input.HammerCreatePlayerBan;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;

public interface IDatabaseGateway extends AutoCloseable {

    void createTables() throws SQLException;

    /**
     * Gets a player's ban record for a server.
     * @param playerUUID The UUID of the player to inspect.
     * @param serverId The Id of the server to get information for.
     * @return A {@link HammerPlayerBan} object if the player has been banned, or <code>null</code>
     * @throws SQLException
     */
    HammerPlayerBan getPlayerBanForServer(UUID playerUUID, int serverId) throws SQLException;

    /**
     * Gets a player's ban record for any server.
     * @param playerUUID The UUID of the player to inspect.
     * @return A {@link List} of {@link HammerPlayerBan} objects
     * @throws SQLException
     */
    List<HammerPlayerBan> getPlayerBans(UUID playerUUID) throws SQLException;

    void updatePlayer(UUID player, String lastName, String ip) throws SQLException;

    void insertPlayerBan(HammerCreatePlayerBan ban) throws SQLException, HammerException;

    void removePlayerBan(UUID player, Integer serverId) throws SQLException;

    void removeAllPlayerBans(UUID player) throws SQLException;

    void removeExpiredPlayerBans(Integer serverId) throws SQLException;

    /**
     * Gets a value indicating whether the specified external ID exists.
     * @param externalId The External ID to check
     * @return <code>true</code> if it already exists, <code>false</code> otherwise.
     * @throws SQLException
     */
    boolean externalIdExists(String externalId) throws SQLException;

    HammerPlayerInfo getPlayerInfo(UUID uuid) throws SQLException;

    /**
     * Gets a {@link HammerPlayerInfo} object from the provided name. It gets the last known UUID.
     * Due to name changes, there is no guarantee that the names will be unique.
     * The search is case-insenstive.
     *
     * @param name The name to search for.
     * @return The player. May be null.
     * @throws SQLException Thrown if the DB finds an error.
     */
    HammerPlayerInfo getLastPlayerInfoFromName(String name) throws SQLException;

    /**
     * Gets a list of {@link HammerPlayerInfo} objects from the provided name.
     * Due to name changes, there is no guarantee that the names will be unique.
     * The search is case-insenstive.
     * 
     * @param name The name to search for.
     * @return The list of players. May be empty.
     * @throws SQLException Thrown if the DB finds an error.
     */
    List<HammerPlayerInfo> getPlayerInfoFromName(String name) throws SQLException;

    void updateServerName(int serverId, String serverName) throws SQLException;

    void updateBanToPermanent(HammerPlayerBan ban) throws SQLException;

    void startTransaction() throws SQLException;

    void commitTransaction() throws SQLException;

    void rollbackTransaction() throws SQLException;
}
