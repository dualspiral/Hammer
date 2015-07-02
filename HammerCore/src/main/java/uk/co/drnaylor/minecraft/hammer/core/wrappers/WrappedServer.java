package uk.co.drnaylor.minecraft.hammer.core.wrappers;

import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

import java.util.UUID;

public interface WrappedServer {

    /**
     * Gets a player by the {@link UUID}
     *
     * @param uuid The {@link UUID}
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    WrappedPlayer getPlayer(UUID uuid);

    /**
     * Gets a player by their last known name
     *
     * @param name The name
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    WrappedPlayer getPlayer(String name);

    /**
     * Gets the console command sender.
     *
     * @return The {@link WrappedCommandSource} that represents the console.
     */
    WrappedCommandSource getConsole();

    /**
     * Sends a message to the entire server.
     *
     * @param message The message to send.
     */
    void sendMessageToServer(HammerText message);

    /**
     * Sends a message to a permission group.
     *
     * @param message The message to send.
     * @param permission The permission group that should see it.
     */
    void sendMessageToPermissionGroup(HammerText message, String permission);

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    void kickAllPlayers(WrappedCommandSource source, String reason);

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    void kickAllPlayers(WrappedCommandSource source, HammerText reason);

    /**
     * Schedules an action for the next tick loop.
     *
     * @param runnable The runnable to run on the next tick loop.
     */
    void scheduleForNextTick(Runnable runnable);

    /**
     * Gets a object that contains methods for obtaining configuration notes.
     *
     * @return Gets a {@link WrappedConfiguration} object.
     */
    WrappedConfiguration getConfiguration();
}
