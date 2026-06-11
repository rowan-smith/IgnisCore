package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.model.ItemDefinition;

public abstract class AbstractIgnisItemStrategy extends AbstractIgnisStrategy implements IgnisItemStrategy {

    protected AbstractIgnisItemStrategy(IgnisStrategyContext context) {
        super(context);
    }

    protected double getCustomDouble(ItemDefinition def, String key, double defaultValue) {
        Object val = def.getCustomData().get(key);
        if (val instanceof Number number) {
            return number.doubleValue();
        }
        return defaultValue;
    }

    protected int getCustomInt(ItemDefinition def, String key, int defaultValue) {
        Object val = def.getCustomData().get(key);
        if (val instanceof Number number) {
            return number.intValue();
        }
        return defaultValue;
    }

    protected boolean getCustomBoolean(ItemDefinition def, String key, boolean defaultValue) {
        Object val = def.getCustomData().get(key);
        if (val instanceof Boolean value) {
            return value;
        }
        return defaultValue;
    }
}
