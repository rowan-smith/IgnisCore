package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.BlockDefinition;

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
