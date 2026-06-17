package dev.rono.igniscore.api.strategy;

import java.util.Objects;

/**
 * Base class for extension strategies with descriptor binding.
 *
 * <p>Subscribe to lifecycle events in the strategy constructor, for example
 * {@code context.eventBus().subscribe(new MyListeners(context))}. During extension loading,
 * unqualified {@code eventBus().subscribe(listener)} calls are automatically scoped to the
 * loading extension id.</p>
 */
public abstract class AbstractIgnisStrategy implements IgnisStrategy {
    private IgnisStrategyDescriptor descriptor;
    protected final IgnisStrategyContext context;

    protected AbstractIgnisStrategy(IgnisStrategyContext context) {
        this.context = context;
    }

    protected AbstractIgnisStrategy(IgnisStrategyDescriptor descriptor) {
        this.descriptor = descriptor;
        this.context = null;
    }

    public void bindDescriptor(IgnisStrategyDescriptor descriptor) {
        this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
    }

    @Override
    public IgnisStrategyDescriptor descriptor() {
        return Objects.requireNonNull(descriptor, "Strategy descriptor has not been bound");
    }
}
