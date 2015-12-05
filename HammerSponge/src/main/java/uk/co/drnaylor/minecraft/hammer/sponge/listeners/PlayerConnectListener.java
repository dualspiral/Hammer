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
package uk.co.drnaylor.minecraft.hammer.sponge.listeners;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerConnectListenerCore;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;
import uk.co.drnaylor.minecraft.hammer.sponge.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.sponge.wrappers.SpongeWrappedPlayer;

public class PlayerConnectListener {

    private final Game game;
    private final PlayerConnectListenerCore eventCore;
    private final Logger logger;
    private UserStorageService storageService = null;

    public PlayerConnectListener(Logger logger, Game game, PlayerConnectListenerCore eventCore) {
        this.logger = logger;
        this.game = game;
        this.eventCore = eventCore;
    }

    private void getServices() {
        if (storageService == null) {
            storageService = game.getServiceManager().provide(UserStorageService.class).get();
        }
    }

    /**
     * Runs when a player has been authenticated with the Mojang services.
     *
     * @param event The event to fire.
     */
    @Listener
    public void onPlayerConnection(ClientConnectionEvent.Auth event) {
        getServices();
        try {
            GameProfile pl = event.getProfile();
            String host = event.getConnection().getAddress().getAddress().getHostAddress();

            User user = storageService.getOrCreate(pl);
            HammerText text = eventCore.handleEvent(
                    new SpongeWrappedPlayer(game, user),
                    host);

            if (text != null) {
                event.setCancelled(true);
                event.setMessage(HammerTextConverter.constructMessage(text));
            }
        } catch (HammerException e) {
            logger.error("Connection to the MySQL database failed. Falling back to the Minecraft ban list.");
            e.printStackTrace();
        }
    }
}
