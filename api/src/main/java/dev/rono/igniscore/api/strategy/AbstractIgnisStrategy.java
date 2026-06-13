package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;

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

    protected ExtensionConfig customConfig(BlockDefinition definition) {
        return definition.getCustomConfig();
    }

    protected ExtensionConfig customConfig(ItemDefinition definition) {
        return definition.getCustomConfig();
    }

    protected double getCustomDouble(BlockDefinition definition, String key, double defaultValue) {
        return customConfig(definition).getDouble(key, defaultValue);
    }

    protected int getCustomInt(BlockDefinition definition, String key, int defaultValue) {
        return customConfig(definition).getInt(key, defaultValue);
    }

    protected boolean getCustomBoolean(BlockDefinition definition, String key, boolean defaultValue) {
        return customConfig(definition).getBoolean(key, defaultValue);
    }

    protected double getCustomDouble(ItemDefinition definition, String key, double defaultValue) {
        return customConfig(definition).getDouble(key, defaultValue);
    }

    protected int getCustomInt(ItemDefinition definition, String key, int defaultValue) {
        return customConfig(definition).getInt(key, defaultValue);
    }

    protected boolean getCustomBoolean(ItemDefinition definition, String key, boolean defaultValue) {
        return customConfig(definition).getBoolean(key, defaultValue);
    }
}
