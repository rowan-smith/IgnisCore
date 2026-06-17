package dev.rono.igniscore.api.event;

/**
 * Platform-neutral event bus for extension lifecycle hooks and integrator observers.
 *
 * <p>Extension strategies subscribe in their constructor. Unqualified {@link #subscribe}
 * calls made while an extension is loading are scoped to that extension automatically.
 * Integrators may subscribe globally (no extension id) to observe all extensions, or pass
 * an explicit extension id for a single extension.</p>
 */
public interface IgnisEventBus {

    void subscribe(OnBlockPlaceListener listener);

    void subscribe(OnBlockClickListener listener);

    void subscribe(OnBlockInteractListener listener);

    void subscribe(OnBlockBreakListener listener);

    void subscribe(OnBlockActivateListener listener);

    void subscribe(OnBlockTickListener listener);

    void subscribe(OnBlockTriggerListener listener);

    void subscribe(OnItemClickListener listener);

    void subscribe(String extensionId, OnBlockPlaceListener listener);

    void subscribe(String extensionId, OnBlockClickListener listener);

    void subscribe(String extensionId, OnBlockInteractListener listener);

    void subscribe(String extensionId, OnBlockBreakListener listener);

    void subscribe(String extensionId, OnBlockActivateListener listener);

    void subscribe(String extensionId, OnBlockTickListener listener);

    void subscribe(String extensionId, OnBlockTriggerListener listener);

    void subscribe(String extensionId, OnItemClickListener listener);

    void unsubscribe(Object listener);
}
