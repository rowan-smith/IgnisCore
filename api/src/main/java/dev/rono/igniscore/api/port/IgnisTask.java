package dev.rono.igniscore.api.port;

/**
 * Handle to a scheduled repeating or delayed task.
 *
 * <p>Obtained from {@link IgnisScheduler}. Cancelled tasks do not run again;
 * repeating tasks stop after {@link #cancel()}.</p>
 */
public interface IgnisTask {

    /**
     * Stops this task from running further executions.
     */
    void cancel();

    /**
     * @return whether this task has been cancelled
     */
    boolean isCancelled();
}
