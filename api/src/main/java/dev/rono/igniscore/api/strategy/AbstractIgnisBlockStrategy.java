package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;

/**
 * Convenience base class for custom block behavior strategies.
 *
 * <p>Extension JARs list a concrete subclass in {@code *-extension.yml}. Subscribe to block
 * lifecycle events in the strategy constructor, for example
 * {@code context.eventBus().subscribe(new MyListeners(context))}.
 * Override {@link IgnisBlockStrategy#profile} to declare default click and ignition behavior.</p>
 */
public abstract class AbstractIgnisBlockStrategy extends AbstractIgnisStrategy implements IgnisBlockStrategy {

    protected AbstractIgnisBlockStrategy(IgnisStrategyContext context) {
        super(context);
    }

    protected AbstractIgnisBlockStrategy(IgnisStrategyDescriptor descriptor) {
        super(descriptor);
    }
}
