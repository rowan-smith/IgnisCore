package dev.rono.igniscore.api.strategy;

/**
 * Base contract for all IgnisCore behavior strategies.
 * Block extensions implement {@link IgnisBlockStrategy}; item extensions implement {@link IgnisItemStrategy}.
 */
public interface IgnisStrategy {

    IgnisStrategyDescriptor descriptor();
}
