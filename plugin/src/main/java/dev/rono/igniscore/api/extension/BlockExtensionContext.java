package dev.rono.igniscore.api.extension;

import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.model.BlockDefinition;

public final class BlockExtensionContext {
    private final BlockExtensionManifest manifest;
    private final BlockDefinition blockDefinition;
    private final IgnisStrategyRegistry strategyRegistry;
    private final IgnisStrategyContext strategyContext;
    private final ExtensionResources resources;

    public BlockExtensionContext(BlockExtensionManifest manifest,
                                 BlockDefinition blockDefinition,
                                 IgnisStrategyRegistry strategyRegistry,
                                 IgnisStrategyContext strategyContext,
                                 ExtensionResources resources) {
        this.manifest = manifest;
        this.blockDefinition = blockDefinition;
        this.strategyRegistry = strategyRegistry;
        this.strategyContext = strategyContext;
        this.resources = resources;
    }

    public BlockExtensionManifest getManifest() {
        return manifest;
    }

    public BlockDefinition getBlockDefinition() {
        return blockDefinition;
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
