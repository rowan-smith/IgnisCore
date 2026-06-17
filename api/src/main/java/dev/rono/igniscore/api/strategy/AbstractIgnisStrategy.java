package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;

import java.util.Objects;

/**
 * Base class for extension strategies with descriptor binding and custom-config helpers.
 *
 * <p>Subclasses receive an {@link IgnisStrategyContext} for runtime services and may optionally
 * be constructed with a pre-built {@link IgnisStrategyDescriptor}. The core binds the descriptor
 * before invoking strategy callbacks when it was not supplied at construction time.</p>
 *
 * @see AbstractIgnisBlockStrategy
 * @see AbstractIgnisItemStrategy
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

    protected AbstractIgnisStrategy(IgnisStrategyDescriptor descriptor, IgnisStrategyContext context) {
        this.descriptor = descriptor;
        this.context = context;
    }

    /**
     * Associates this strategy instance with registry metadata.
     *
     * <p>Called by the core when a descriptor was not provided at construction time.</p>
     *
     * @param descriptor strategy identity and provenance
     */
    public void bindDescriptor(IgnisStrategyDescriptor descriptor) {
        this.descriptor = Objects.requireNonNull(descriptor, "descriptor");
    }

    /**
     * Returns the bound descriptor for this strategy.
     *
     * @return non-null descriptor previously set via constructor or {@link #bindDescriptor}
     * @throws NullPointerException when no descriptor has been bound
     */
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
