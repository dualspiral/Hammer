package uk.co.drnaylor.minecraft.hammer.bukkit.wrappers;

import org.bukkit.Bukkit;
import uk.co.drnaylor.minecraft.hammer.bukkit.HammerBukkitPlugin;
import uk.co.drnaylor.minecraft.hammer.core.wrappers.WrappedScheduler;

public class BukkitWrappedScheduler implements WrappedScheduler {

    private final HammerBukkitPlugin plugin;

    public BukkitWrappedScheduler(HammerBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runSyncNow(Runnable runnable) {
        Bukkit.getScheduler().runTask(plugin, runnable);
    }

    @Override
    public void runAsyncNow(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    @Override
    public void createAsyncRecurringTask(Runnable runnable, int ticks) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, ticks, ticks);
    }
}
