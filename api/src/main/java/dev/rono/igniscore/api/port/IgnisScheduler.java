package dev.rono.igniscore.api.port;

/**
 * Platform-neutral scheduler for delayed and repeating tasks.
 */
public interface IgnisScheduler {

    IgnisTask runLater(IgnisLocation location, Runnable task, long delayTicks);

    IgnisTask runRepeating(IgnisLocation location, Runnable task, long delayTicks, long periodTicks);

    void runGlobal(Runnable task);

    void runGlobalLater(Runnable task, long delayTicks);
}
