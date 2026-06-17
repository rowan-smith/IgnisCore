package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;

/**
 * Convenience base class for custom block behavior strategies.
 *
 * <p>Extension JARs list a concrete subclass in {@code *-extension.yml}. Implement
 * {@link IgnisBlockStrategy} callbacks and optionally override {@link IgnisBlockStrategy#profile}
 * to declare default click and ignition behavior.</p>
 *
 * @see IgnisBlockStrategy
 * @see AbstractIgnisStrategy
 */
public abstract class AbstractIgnisBlockStrategy extends AbstractIgnisStrategy implements IgnisBlockStrategy {

    protected AbstractIgnisBlockStrategy(IgnisStrategyContext context) {
        super(context);
    }

    protected AbstractIgnisBlockStrategy(IgnisStrategyDescriptor descriptor) {
        super(descriptor);
    }

    protected AbstractIgnisBlockStrategy(IgnisStrategyDescriptor descriptor, IgnisStrategyContext context) {
        super(descriptor, context);
    }
}
