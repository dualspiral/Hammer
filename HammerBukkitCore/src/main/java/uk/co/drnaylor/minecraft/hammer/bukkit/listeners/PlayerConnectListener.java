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
package uk.co.drnaylor.minecraft.hammer.bukkit.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;
import uk.co.drnaylor.minecraft.hammer.bukkit.text.HammerTextConverter;
import uk.co.drnaylor.minecraft.hammer.bukkit.wrappers.BukkitWrappedPlayer;
import uk.co.drnaylor.minecraft.hammer.core.exceptions.HammerException;
import uk.co.drnaylor.minecraft.hammer.core.listenercores.PlayerConnectListenerCore;
import uk.co.drnaylor.minecraft.hammer.core.text.HammerText;

public class PlayerConnectListener implements Listener {

    private final HammerBukkitPlugin plugin;
    private final PlayerConnectListenerCore eventCore;

    public PlayerConnectListener(HammerBukkitPlugin plugin) {
        this.plugin = plugin;
        this.eventCore = new PlayerConnectListenerCore(plugin.getHammerCore());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerConnect(AsyncPlayerPreLoginEvent event) {
        try {
            HammerText text = eventCore.handleEvent(
                    new BukkitWrappedPlayer(Bukkit.getOfflinePlayer(event.getUniqueId())),
                    event.getAddress().getHostAddress());

            if (text != null) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, HammerTextConverter.constructMessage(text));
            }
        } catch (HammerException e) {
            plugin.getLogger().severe("Connection to the MySQL database failed. Falling back to the Minecraft ban list.");
            e.printStackTrace();
        }
    }
}
