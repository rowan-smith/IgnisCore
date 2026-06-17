package dev.rono.igniscore.api.strategy;

/**
 * Convenience base class for custom item behavior strategies.
 *
 * <p>Extension JARs list a concrete subclass in {@code *-extension.yml}. Subscribe to item
 * lifecycle events in the strategy constructor, for example
 * {@code context.eventBus().subscribe(new MyListeners(context))}.</p>
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
