package dev.rono.igniscore.api.port;

/**
 * Platform-specific hooks invoked by the shared runtime during enable/disable.
 *
 * <p>Adapter modules may supply an implementation to register commands, bind
 * listeners, or perform other host setup that the core runtime cannot do in a
 * platform-neutral way.</p>
 */
public interface IgnisPlatformIntegration {

    /**
     * Registers platform commands after the runtime has started.
     *
     * <p>Default implementation is a no-op; override when the adapter needs
     * to expose slash commands or similar host entry points.</p>
     */
    default void registerCommands() {
    }

    /**
     * Called when the Ignis runtime finishes enabling on this host.
     *
     * <p>Use for one-time setup that depends on the adapter being fully booted.</p>
     */
    void onRuntimeEnable();

    /**
     * Called when the Ignis runtime is shutting down on this host.
     *
     * <p>Use to release host resources registered during enable.</p>
     */
    void onRuntimeDisable();
}
