package dev.rono.igniscore.api.event;

/**
 * Platform-neutral event bus for extension lifecycle hooks and integrator observers.
 *
 * <p>Extension strategies subscribe in their constructor, for example
 * {@code context.eventBus().subscribe(new MyOnBlockPlaceListener(context))}.
 * During extension loading, unqualified {@code eventBus().subscribe(listener)} calls are
 * automatically scoped to the loading extension id. Integrators may subscribe globally (no
 * extension id) to observe all extensions, or pass an explicit extension id for a single
 * extension.</p>
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
