package ru.civwars;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import ru.lib27.annotation.NotNull;

public class TaskMaster {

    private static CivWars plugin;

    private static ScheduledThreadPoolExecutor generalScheduledThreadPool;
    
    public static void init(@NotNull CivWars plugin) {
        if (TaskMaster.plugin != null) {
            return;
        }
        TaskMaster.plugin = plugin;
        
        TaskMaster.generalScheduledThreadPool = new ScheduledThreadPoolExecutor(Config.GENERAL_THREAD_POOL_SIZE, new PriorityThreadFactory("GerenalSTPool", Thread.NORM_PRIORITY));
    }

    private TaskMaster() {
    }
    
    @NotNull
    public static BukkitTask runTask(@NotNull Runnable task) {
        return Bukkit.getScheduler().runTask(CivWars.get(), task);
    }

    @NotNull
    public static BukkitTask runTaskAsynchronously(@NotNull Runnable task) {
        return Bukkit.getScheduler().runTaskAsynchronously(CivWars.get(), task);
    }

    @NotNull
    public static BukkitTask runTaskLater(@NotNull Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLater(CivWars.get(), task, delay);
    }

    @NotNull
    public static BukkitTask runTaskLaterAsynchronously(@NotNull Runnable task, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(CivWars.get(), task, delay);
    }

    @NotNull
    public static BukkitTask runTaskTimer(@NotNull Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(CivWars.get(), task, delay, period);
    }

    @NotNull
    public static BukkitTask runTaskTimerAsynchronously(@NotNull Runnable task, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(CivWars.get(), task, delay, period);
    }

    public static ScheduledFuture<?> scheduleGeneral(@NotNull Runnable task, long delay) {
        try {
            if (delay < 0) {
                delay = 0;
            }
            return TaskMaster.generalScheduledThreadPool.schedule(task, delay, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            return null;
        }
    }

    public static ScheduledFuture<?> scheduleGeneralAtFixedRate(@NotNull Runnable task, long delay, long period) {
        try {
            if (delay < 0) {
                delay = 0;
            }
            if (period < 0) {
                period = 0;
            }
            return TaskMaster.generalScheduledThreadPool.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ex) {
            return null;
        }
    }

    private static class PriorityThreadFactory implements ThreadFactory {

        private final String name;
        private final int priority;
        private final AtomicInteger number = new AtomicInteger(1);
        private final ThreadGroup group;

        public PriorityThreadFactory(@NotNull String name, int priority) {
            this.name = name;
            this.priority = priority;
            this.group = new ThreadGroup(name);
        }

        @Override
        public Thread newThread(@NotNull Runnable runnable) {
            Thread t = new Thread(this.group, runnable);
            t.setName(this.name + "-" + this.number.getAndIncrement());
            t.setPriority(this.priority);
            return t;
        }

        public ThreadGroup getGroup() {
            return this.group;
        }
    }
    
}
