package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.ItemDefinition;

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
