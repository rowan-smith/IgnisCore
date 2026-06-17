package dev.rono.igniscore.api.port;

/**
 * Platform-neutral scheduler for delayed and repeating tasks.
 *
 * <p>Tick delays follow Minecraft server tick semantics (20 ticks per second).
 * Location-scoped tasks are tied to a world's chunk loading where the platform
 * supports it; global tasks run regardless of player proximity.</p>
 */
public interface IgnisScheduler {

    /**
     * Runs a task once after a delay, scoped to the given world's lifecycle.
     *
     * @param location world anchor for chunk-tied scheduling
     * @param task work to execute
     * @param delayTicks ticks to wait before the first execution
     * @return cancellable task handle
     */
    IgnisTask runLater(IgnisLocation location, Runnable task, long delayTicks);

    /**
     * Runs a task repeatedly after an initial delay, scoped to the given world.
     *
     * @param location world anchor for chunk-tied scheduling
     * @param task work to execute on each period
     * @param delayTicks ticks to wait before the first execution
     * @param periodTicks ticks between subsequent executions
     * @return cancellable task handle
     */
    IgnisTask runRepeating(IgnisLocation location, Runnable task, long delayTicks, long periodTicks);

    /**
     * Runs a task on the next server tick, globally (not chunk-scoped).
     *
     * @param task work to execute
     */
    void runGlobal(Runnable task);

    /**
     * Runs a task once after a delay, globally (not chunk-scoped).
     *
     * @param task work to execute
     * @param delayTicks ticks to wait before execution
     */
    void runGlobalLater(Runnable task, long delayTicks);
}
