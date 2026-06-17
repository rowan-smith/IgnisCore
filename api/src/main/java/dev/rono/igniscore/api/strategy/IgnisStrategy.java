package dev.rono.igniscore.api.strategy;

/**
 * Base contract for all IgnisCore behavior strategies.
 *
 * <p>Block extensions implement {@link IgnisBlockStrategy}; item extensions implement
 * {@link IgnisItemStrategy}. Behavior is registered through the event bus in
 * {@link #registerEvents()} rather than interface overrides.</p>
 */
public interface IgnisStrategy {

    /**
     * Returns identity and provenance metadata for this strategy.
     *
     * @return descriptor bound at registration or construction time
     */
    IgnisStrategyDescriptor descriptor();

    /**
     * Subscribes to lifecycle events for this extension. Called by the core after the descriptor
     * is bound and before the strategy is registered.
     */
    default void registerEvents() {
    }
}
