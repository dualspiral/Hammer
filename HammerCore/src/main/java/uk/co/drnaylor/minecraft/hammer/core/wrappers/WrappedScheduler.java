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
package uk.co.drnaylor.minecraft.hammer.core.wrappers;

/**
 * Wraps the API specific schedulers
 */
public interface WrappedScheduler {

    /**
     * Runs a task as soon as possible on the main thread.
     *
     * @param runnable The {@link Runnable} to run.
     */
    void runSyncNow(Runnable runnable);

    /**
     * Runs a task as soon as possible on an async thead.
     *
     * @param runnable The {@link Runnable} to run.
     */
    void runAsyncNow(Runnable runnable);

    /**
     * Sets up a recurring task that is asynchronus
     *  @param runnable The runnable.
     * @param seconds Number of seconds between runs.
     * @return The {@link WrappedSchedulerTask}
     */
    WrappedSchedulerTask createAsyncRecurringTask(Runnable runnable, int seconds);
}
