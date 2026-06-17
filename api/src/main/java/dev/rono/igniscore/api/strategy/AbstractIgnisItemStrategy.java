package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.ItemDefinition;

/**
 * Convenience base class for custom item behavior strategies.
 *
 * <p>Extension JARs list a concrete subclass in {@code *-extension.yml}. Implement
 * {@link IgnisItemStrategy} callbacks for use, interact, and lifecycle events on custom items.</p>
 *
 * @see IgnisItemStrategy
 * @see AbstractIgnisStrategy
 */
public abstract class AbstractIgnisItemStrategy extends AbstractIgnisStrategy implements IgnisItemStrategy {

    protected AbstractIgnisItemStrategy(IgnisStrategyContext context) {
        super(context);
    }

    protected AbstractIgnisItemStrategy(IgnisStrategyDescriptor descriptor) {
        super(descriptor);
    }

    protected AbstractIgnisItemStrategy(IgnisStrategyDescriptor descriptor, IgnisStrategyContext context) {
        super(descriptor, context);
    }
}
