package dev.rono.igniscore.api.strategy;

import java.util.Objects;

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

    protected AbstractIgnisStrategy(IgnisStrategyDescriptor descriptor, IgnisStrategyContext context) {
        this.descriptor = descriptor;
        this.context = context;
    }

    public void bindDescriptor(IgnisStrategyDescriptor descriptor) {
        this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
    }

    @Override
    public IgnisStrategyDescriptor descriptor() {
        return Objects.requireNonNull(descriptor, "Strategy descriptor has not been bound");
    }
}
