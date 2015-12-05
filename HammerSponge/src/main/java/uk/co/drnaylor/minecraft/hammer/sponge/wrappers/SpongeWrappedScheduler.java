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

import org.spongepowered.api.Game;
import org.spongepowered.api.scheduler.Task;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedScheduler;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedSchedulerTask;
import uk.co.drnaylor.minecraft.hammer.sponge.HammerSponge;

import java.util.concurrent.TimeUnit;

public class SpongeWrappedScheduler implements WrappedScheduler {
    private final HammerSponge plugin;
    private final Game game;

    public SpongeWrappedScheduler(HammerSponge plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Override
    public void runSyncNow(Runnable runnable) {
        game.getScheduler().createTaskBuilder().execute(runnable).submit(plugin);
    }

    @Override
    public void runAsyncNow(Runnable runnable) {
        game.getScheduler().createTaskBuilder().async().execute(runnable).submit(plugin);
    }

    @Override
    public WrappedSchedulerTask createAsyncRecurringTask(Runnable runnable, int seconds) {
        Task t = game.getScheduler().createTaskBuilder().async().interval(seconds, TimeUnit.SECONDS)
                .delay(seconds, TimeUnit.SECONDS).execute(runnable).submit(plugin);

        return new SpongeWrappedSchedulerTask(t, runnable);
    }
}
