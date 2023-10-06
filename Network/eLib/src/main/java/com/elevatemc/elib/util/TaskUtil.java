package com.elevatemc.elib.util;


import com.elevatemc.elib.eLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;


import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("ALL")
public class TaskUtil {

    private static final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(8);
    private static final JavaPlugin javaPlugin = eLib.getInstance();
    private static final BukkitScheduler scheduler = Bukkit.getScheduler();

    public static void scheduleAtFixedRateOnPool(Runnable runnable, long delay, long period, TimeUnit timeUnit) {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(runnable, delay, period, timeUnit);
    }

    public static void scheduleOnPool(Runnable runnable, long delay, TimeUnit timeUnit) {
        scheduledThreadPoolExecutor.schedule(runnable, delay, timeUnit);
    }

    public static void executeWithPool(Runnable runnable) {
        scheduledThreadPoolExecutor.execute(runnable);
    }

    public static void executeWithPoolIfRequired(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            scheduledThreadPoolExecutor.execute(runnable);
            return;
        }
        runnable.run();
    }

    public static void runSync(Runnable runnable) {
        scheduler.runTask(javaPlugin, runnable);
    }

    public BukkitTask runTask(Runnable runnable) {
        return scheduler.runTask(javaPlugin, runnable);
    }

    public static BukkitTask runTaskAsynchronously(Runnable runnable) {
        return scheduler.runTaskAsynchronously(javaPlugin, runnable);
    }

    public static BukkitTask runTaskTimer(Runnable runnable, int initialDelay, int scheduleDelay) {
        return scheduler.runTaskTimer(javaPlugin, runnable, initialDelay, scheduleDelay);
    }

    public static BukkitTask runTaskTimerAsynchronously(Runnable runnable, int initialDelay, int scheduleDelay) {
        return scheduler.runTaskTimerAsynchronously(javaPlugin, runnable, initialDelay, scheduleDelay);
    }

    public static BukkitTask runTaskLater(Runnable runnable, int delay) {
        return scheduler.runTaskLater(javaPlugin, runnable, delay);
    }

    public static BukkitTask runTaskLaterAsynchronously(Runnable runnable, int delay) {
        return scheduler.runTaskLaterAsynchronously(javaPlugin, runnable, delay);
    }

    public static <T> Future<T> callMethodSync(Callable<T> callable) {
        return scheduler.callSyncMethod(javaPlugin, callable);
    }

    public static <T> T callMethodSyncFetched(Callable<T> callable) {
        Future<T> future = callMethodSync(callable);
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T, V> CompletableFuture<V> andThenSync(CompletableFuture<T> input, Function<T, V> function) {
        return input.thenApply((T in) -> callMethodSyncFetched(() -> function.apply(in)));
    }

    public static <T> CompletableFuture<Void> andThenSync(CompletableFuture<T> input, Consumer<T> consumer) {
        return input.thenAccept((T in) -> callMethodSyncFetched(() -> {
            consumer.accept(in);
            return in;
        }));
    }

    public static void shutdownThreadPoolExecutor() {
        scheduledThreadPoolExecutor.shutdown();
    }
}

