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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.ban.BanService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.util.ban.BanTypes;
import uk.co.drnaylor.minecraft.hammer.core.data.HammerPlayerInfo;
import uk.co.drnaylor.minecraft.hammer.core.handlers.DatabaseConnection;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerTextBuilder;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedCommandSource;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public class SpongeWrappedPlayer implements WrappedPlayer {

    private final GameProfile gameProfile;

    public SpongeWrappedPlayer(User user) {
        this(user.getProfile());
    }

    public SpongeWrappedPlayer(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }

    private static User getUser(GameProfile gameProfile) {
        return Sponge.getServer().getPlayer(gameProfile.getUniqueId()).map(x -> (User) x)
                .orElseGet(() -> Sponge.getServiceManager().provideUnchecked(UserStorageService.class).getOrCreate(gameProfile));
    }

    /**
     * Gets the name of the player.
     *
     * @return The name.
     */
    @Override
    public String getName() {
        return getUser(gameProfile).getName();
    }

    /**
     * Gets the Unique Identifier of the player.
     *
     * @return The {@link UUID}
     */
    @Override
    public UUID getUUID() {
        return getUser(gameProfile).getUniqueId();
    }

    /**
     * Sends a message to the player
     *
     * @param message The message
     */
    @Override
    public void sendMessage(HammerText message) {
        Optional<Player> onlinePlayer = getUser(gameProfile).getPlayer();
        if (onlinePlayer.isPresent()) {
            onlinePlayer.get().sendMessage(HammerTextConverter.constructMessage(message));
        }

    }

    /**
     * Sends a message to the player
     *
     * @param message The message
     */
    @Override
    public void sendMessage(String message) {
        Optional<Player> onlinePlayer = getUser(gameProfile).getPlayer();
        if (onlinePlayer.isPresent()) {
            onlinePlayer.get().sendMessage(ChatTypes.SYSTEM, Text.of(message));
        }
    }

    /**
     * Gets whether the player has the specified permission.
     *
     * @param permission The permission
     * @return <code>true</code> if the player has the permission specified.
     */
    @Override
    public boolean hasPermission(String permission) {
        return getUser(gameProfile).hasPermission(permission);
    }

    /**
     * Bans the player with the specified reason
     *
     * @param reason The reason
     */
    @Override
    public void ban(WrappedCommandSource source, HammerText reason) {
        Ban.Builder builder = Ban.builder().type(BanTypes.PROFILE)
                .reason(HammerTextConverter.constructLiteral(reason)).profile(getUser(gameProfile).getProfile());
        if (source instanceof SpongeWrappedPlayer) {
            Optional<Player> sourceplayer = ((SpongeWrappedPlayer) source).getSpongePlayer();
            if (sourceplayer.isPresent()) {
                builder.source(sourceplayer.get());
            }
        } else if (source instanceof SpongeWrappedConsole) {
            builder.source(((SpongeWrappedConsole) source).getSpongeSource());
        }

        HammerSponge.getBanService().get().addBan(builder.build());

        kick(reason);
    }

    /**
     * Bans the player with the specified reason
     *
     * @param reason The reason
     */
    @Override
    public void ban(WrappedCommandSource source, String reason) {
        ban(source, new HammerTextBuilder().add(reason).build());
    }

    /**
     * Unbans the player
     */
    @Override
    public void unban() {
        BanService service = HammerSponge.getBanService().get();
        Optional<Ban.Profile> obp = service.getBanFor(getUser(gameProfile).getProfile());
        if (obp.isPresent()) {
            service.removeBan(obp.get());
        }
    }

    /**
     * Gets whether the player is banned.
     *
     * @return Whether the player is banned or not.
     */
    @Override
    public boolean isBanned() {
        return HammerSponge.getBanService().get().isBanned(getUser(gameProfile).getProfile());
    }

    /**
     * Kicks a player with the specified reason.
     *
     * @param reason The reason.
     */
    @Override
    public void kick(HammerText reason) {
        Optional<Player> onlinePlayer = getUser(gameProfile).getPlayer();
        if (onlinePlayer.isPresent()) {
            onlinePlayer.get().kick(HammerTextConverter.constructMessage(reason));
        }
    }

    /**
     * Kicks a player with the specified reason.
     *
     * @param reason The reason.
     */
    @Override
    public void kick(String reason) {
        Optional<Player> onlinePlayer = getUser(gameProfile).getPlayer();
        if (onlinePlayer.isPresent()) {
            onlinePlayer.get().kick(Text.of(reason));
        }
    }

    @Override
    public boolean isOnline() {
        return getUser(gameProfile).isOnline();
    }

    @Override
    public HammerPlayerInfo getHammerPlayer() {
        User user = getUser(gameProfile);
        Optional<Player> pl = user.getPlayer();
        if (pl.isPresent()) {
            InetSocketAddress addr = pl.get().getConnection().getAddress();
            String ip = addr.toString().substring(1).split(":")[0];
            return new HammerPlayerInfo(pl.get().getUniqueId(), pl.get().getName(), ip);
        } else {
            try (DatabaseConnection c = HammerSponge.getInstance().getCore().getDatabaseConnection()) {
                return c.getPlayerHandler().getPlayer(user.getUniqueId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * Gets the Sponge {@link User}
     *
     * @return The {@link User}
     */
    public User getSpongeUser() {
        return getUser(gameProfile);
    }

    private Optional<Player> getSpongePlayer() {
        return getUser(gameProfile).getPlayer();
    }
}
