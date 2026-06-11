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
