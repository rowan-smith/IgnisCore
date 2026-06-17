package dev.rono.igniscore.api.strategy;

/**
 * Base contract for all IgnisCore behavior strategies.
 *
 * <p>Block extensions implement {@link IgnisBlockStrategy}; item extensions implement
 * {@link IgnisItemStrategy}. Behavior is registered through scoped event bus subscriptions
 * in the strategy constructor.</p>
 */
public interface IgnisStrategy {

    /**
     * Returns identity and provenance metadata for this strategy.
     *
     * @return descriptor bound at registration or construction time
     */
    IgnisStrategyDescriptor descriptor();
}
