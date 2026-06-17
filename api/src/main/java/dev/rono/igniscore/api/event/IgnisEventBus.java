package dev.rono.igniscore.api.event;

/**
 * Platform-neutral event bus for extension lifecycle hooks and integrator observers.
 *
 * <p>Extension strategies subscribe in {@link dev.rono.igniscore.api.strategy.IgnisStrategy#registerEvents()}
 * using scoped helpers on {@link dev.rono.igniscore.api.strategy.AbstractIgnisStrategy}, or call
 * {@link #subscribe(String, OnItemClickListener)} with their manifest extension id.</p>
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
