package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.model.BlockDefinition;

public abstract class AbstractIgnisStrategy implements IgnisStrategy {
    private final IgnisStrategyDescriptor descriptor;
    protected final IgnisStrategyContext context;

    protected AbstractIgnisStrategy(IgnisStrategyDescriptor descriptor) {
        this(descriptor, null);
    }

    protected AbstractIgnisStrategy(IgnisStrategyDescriptor descriptor, IgnisStrategyContext context) {
        this.descriptor = descriptor;
        this.context = context;
    }

    @Override
    public IgnisStrategyDescriptor descriptor() {
        return descriptor;
    }

    protected double getCustomDouble(BlockDefinition def, String key, double defaultValue) {
        Object val = def.getCustomData().get(key);
        if (val instanceof Number number) {
            return number.doubleValue();
        }
        return defaultValue;
    }

    protected int getCustomInt(BlockDefinition def, String key, int defaultValue) {
        Object val = def.getCustomData().get(key);
        if (val instanceof Number number) {
            return number.intValue();
        }
        return defaultValue;
    }

    protected boolean getCustomBoolean(BlockDefinition def, String key, boolean defaultValue) {
        Object val = def.getCustomData().get(key);
        if (val instanceof Boolean value) {
            return value;
        }
        return defaultValue;
    }
}
