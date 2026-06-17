package dev.rono.igniscore.api.strategy;

/**
 * Base contract for all IgnisCore behavior strategies.
 *
 * <p>Block extensions implement {@link IgnisBlockStrategy}; item extensions implement
 * {@link IgnisItemStrategy}. Every strategy exposes an {@link IgnisStrategyDescriptor} for
 * registration and diagnostics.</p>
 */
public interface IgnisStrategy {

    /**
     * Returns identity and provenance metadata for this strategy.
     *
     * @return descriptor bound at registration or construction time
     */
    IgnisStrategyDescriptor descriptor();
}
