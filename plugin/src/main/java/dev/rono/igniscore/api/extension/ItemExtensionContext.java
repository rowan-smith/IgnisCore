package dev.rono.igniscore.api.extension;

import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.model.ItemDefinition;

public final class ItemExtensionContext {
    private final ItemExtensionManifest manifest;
    private final ItemDefinition itemDefinition;
    private final IgnisStrategyRegistry strategyRegistry;
    private final IgnisStrategyContext strategyContext;
    private final ExtensionResources resources;

    public ItemExtensionContext(ItemExtensionManifest manifest,
                                ItemDefinition itemDefinition,
                                IgnisStrategyRegistry strategyRegistry,
                                IgnisStrategyContext strategyContext,
                                ExtensionResources resources) {
        this.manifest = manifest;
        this.itemDefinition = itemDefinition;
        this.strategyRegistry = strategyRegistry;
        this.strategyContext = strategyContext;
        this.resources = resources;
    }

    public ItemExtensionManifest getManifest() {
        return manifest;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public IgnisStrategyContext getStrategyContext() {
        return strategyContext;
    }

    public ExtensionResources getResources() {
        return resources;
    }

    public void registerStrategy(IgnisStrategy strategy) {
        IgnisStrategyDescriptor descriptor = IgnisStrategyDescriptor.of(
                strategy.descriptor().getId(),
                strategy.descriptor().getName(),
                strategy.descriptor().getVersion(),
                strategy.descriptor().getAuthor(),
                manifest.getId()
        );
        strategyRegistry.register(descriptor, strategy);
    }
}
