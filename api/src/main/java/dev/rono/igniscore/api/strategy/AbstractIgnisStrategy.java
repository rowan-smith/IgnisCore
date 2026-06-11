package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.model.BlockDefinition;

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
