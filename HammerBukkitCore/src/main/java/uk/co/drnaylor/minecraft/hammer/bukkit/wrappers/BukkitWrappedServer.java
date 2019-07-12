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
package uk.co.drnaylor.minecraft.hammer.bukkit.wrappers;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;
import uk.co.drnaylor.minecraft.hammer.bukkit.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.*;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class BukkitWrappedServer implements WrappedServer {

    private final Server server;
    private final HammerBukkitPlugin plugin;
    private final WrappedScheduler scheduler;
    private final BukkitWrappedLogger logger;
    private final Path logFolder;

    public BukkitWrappedServer(HammerBukkitPlugin plugin, Server server) {
        this.plugin = plugin;
        this.server = server;
        this.scheduler = new BukkitWrappedScheduler(plugin);
        this.logger = new BukkitWrappedLogger(plugin.getLogger());
        this.logFolder = plugin.getDataFolder().toPath().resolve("logs");
        try {
            Files.createDirectories(this.logFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets a player by the {@link UUID}
     *
     * @param uuid The {@link UUID}
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    @Override
    public WrappedPlayer getPlayer(UUID uuid) {
        return BukkitWrappedPlayer.of(uuid);
    }

    /**
     * Gets a player by their last known name
     *
     * @param name The name
     * @return The {@link WrappedPlayer} if it exists, otherwise <code>null</code>
     */
    @Override
    public WrappedPlayer getPlayer(String name) {
        return BukkitWrappedPlayer.ofOnlinePlayer(name);
    }

    @Override
    public List<WrappedPlayer> getOnlinePlayers() {
        return plugin.getOnlinePlayers().stream().map(BukkitWrappedPlayer::new).collect(Collectors.toList());
    }

    /**
     * Gets the console command sender.
     *
     * @return The {@link WrappedCommandSource} that represents the console.
     */
    public WrappedCommandSource getConsole() {
        return new BukkitWrappedConsole(server.getConsoleSender());
    }

    /**
     * Sends a message to the entire server.
     *
     * @param message The message to send.
     */
    @Override
    public void sendMessageToServer(HammerText message) {
        server.broadcastMessage(HammerTextConverter.constructMessage(message));
    }

    /**
     * Sends a message to a permission group.
     *
     * @param message    The message to send.
     * @param permission The permission group that should see it.
     */
    @Override
    public void sendMessageToPermissionGroup(HammerText message, String permission) {
        server.broadcast(HammerTextConverter.constructMessage(message), permission);
    }

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    @Override
    public void kickAllPlayers(final WrappedCommandSource source, String reason) {
        final String r = ChatColor.translateAlternateColorCodes('&', reason);
        new ArrayList<>(plugin.getOnlinePlayers()).forEach(pl ->{
            if (pl.isOnline() && source.getUUID() != pl.getUniqueId()) {
                pl.kickPlayer(r);
            }
        });
    }

    /**
     * Kicks all players from the server, apart from the executing user.
     *
     * @param source The {@link WrappedCommandSource} that kicked the user.
     * @param reason The reason for the kick.
     */
    @Override
    public void kickAllPlayers(WrappedCommandSource source, HammerText reason) {
        kickAllPlayers(source, HammerTextConverter.constructMessage(reason));
    }

    @Override
    public void setWhitelist(boolean set) {
        server.setWhitelist(set);
    }

    @Override
    public WrappedScheduler getScheduler() {
        return this.scheduler;
    }

    @Override
    public WrappedLogger getLogger() {
        return logger;
    }

    @Override
    public Path getDataFolder() {
        return plugin.getDataFolder().toPath();
    }

    @Override
    public Path getLogFolder() {
        return logFolder;
    }

    /**
     * Bans an IP address from the server.
     *
     * @param ip     The {@link InetAddress} to ban
     * @param reason The reason for banning.
     */
    @Override
    public void banIP(InetAddress ip, String reason) {
        server.banIP(ip.getHostAddress());
        HammerBukkitPlugin.getPlugin().getOnlinePlayers().stream()
                .filter(p -> p.getAddress().getAddress().getHostAddress().equals(ip.getHostAddress()))
                .forEach(player -> player.kickPlayer(reason));
    }

    /**
     * Unbans an IP address from the server.
     *
     * @param ip The {@link InetAddress} to unban
     */
    @Override
    public void unbanIP(InetAddress ip) {
        server.unbanIP(ip.getHostAddress());
    }

    /**
     * Returns whether an IP is banned from the server.
     *
     * @param ip The IP to check.
     * @return <code>true</code> if the IP has been banned, <code>false</code> otherwise.
     */
    @Override
    public boolean isIPBanned(InetAddress ip) {
        return server.getIPBans().contains(ip.getHostAddress());
    }
}
