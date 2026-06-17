package dev.rono.igniscore.api.port;

/**
 * Platform-specific hooks invoked by the shared runtime during enable/disable.
 */
public interface IgnisPlatformIntegration {

    default void registerCommands() {
    }

    void onRuntimeEnable();

    void onRuntimeDisable();
}
