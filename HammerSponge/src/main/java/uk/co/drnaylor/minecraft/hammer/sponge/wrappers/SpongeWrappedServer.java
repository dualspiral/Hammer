/*
 * This file is part of Hammer, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Daniel Naylor
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package uk.co.drnaylor.minecraft.hammer.sponge.wrappers;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.sink.MessageSinks;
import org.spongepowered.api.command.CommandSource;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.*;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class SpongeWrappedServer implements WrappedServer {

    private final Game game;
    private final HammerSponge plugin;
    private final WrappedScheduler scheduler;
    private final WrappedLogger logger;
    private final File logFolder;

    public SpongeWrappedServer(HammerSponge plugin, Game game, Logger logger) {
        this.logger = new SpongeWrappedLogger(logger);
        this.plugin = plugin;
        this.game = game;
        this.scheduler = new SpongeWrappedScheduler(plugin, game);
        this.logFolder = new File("logs" + File.separator + "Hammer");
        this.logFolder.mkdirs();
    }

    /**
     * Gets a player by the {@link UUID}
     *
     * @param uuid The {@link UUID}
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    @Override
    public WrappedPlayer getPlayer(UUID uuid) {
        UserStorageService service = game.getServiceManager().provide(UserStorageService.class).get();
        Optional<User> player = service.get(uuid);
        if (player.isPresent()) {
            return new SpongeWrappedPlayer(player.get());
        }

        return null;
    }

    /**
     * Gets a player by their last known name
     *
     * @param name The name
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    @Override
    public WrappedPlayer getPlayer(String name) {
        UserStorageService service = game.getServiceManager().provide(UserStorageService.class).get();
        Optional<User> player = service.get(name);
        if (player.isPresent()) {
            return new SpongeWrappedPlayer(player.get());
        }

        return null;
    }

    @Override
    public List<WrappedPlayer> getOnlinePlayers() {
        return game.getServer().getOnlinePlayers().stream().map(p -> new SpongeWrappedPlayer(p)).collect(Collectors.toList());
    }

    /**
     * Gets the console command sender.
     *
     * @return The {@link WrappedCommandSource} that represents the console.
     */
    public WrappedCommandSource getConsole() {
        return new SpongeWrappedConsole(game.getServer().getConsole());
    }

    /**
     * Sends a message to the entire server.
     *
     * @param message The message to send.
     */
    @Override
    public void sendMessageToServer(HammerText message) {
        game.getServer().getBroadcastSink().sendMessage(HammerTextConverter.constructMessage(message));
    }

    /**
     * Sends a message to a permission group.
     *
     * @param message    The message to send.
     * @param permission The permission group that should see it.
     */
    @Override
    public void sendMessageToPermissionGroup(HammerText message, String permission) {
        // Get the players with the permission group.
        Set<CommandSource> targets = game.getServer().getOnlinePlayers().stream().filter(p -> p.hasPermission(permission)).collect(Collectors.toSet());

        // Add the console.
        targets.add(game.getServer().getConsole());

        // Send
        MessageSinks.to(targets).sendMessage(HammerTextConverter.constructMessage(message));
    }

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    @Override
    public void kickAllPlayers(WrappedCommandSource source, String reason) {
        kickAllPlayers(source, new HammerTextBuilder().add(reason).build());
    }

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    @Override
    public void kickAllPlayers(WrappedCommandSource source, HammerText reason) {
        // Copy the collection so that we can operate on it, in case the implemenation removes
        // players from the collection as we kick them.
        Collection<Player> players = new ArrayList<>(game.getServer().getOnlinePlayers());

        // We use a while in case the loop has been mutated.
        for (Player player : players) {
            if (player.getUniqueId() == source.getUUID()) {
                // Don't kick the current player.
                continue;
            }

            player.kick(HammerTextConverter.constructMessage(reason));
        }
    }

    @Override
    public void setWhitelist(boolean set) {
        game.getServer().setHasWhitelist(set);
    }

    @Override
    public WrappedScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public WrappedLogger getLogger() {
        return logger;
    }

    @Override
    public File getDataFolder() {
        return plugin.getDefaultConfig().getParentFile();
    }

    @Override
    public File getLogFolder() {
        return logFolder;
    }
}
