package dev.rono.igniscore.api.port;

/**
 * Handle to a scheduled repeating or delayed task.
 */
public interface IgnisTask {

    void cancel();

    boolean isCancelled();
}
