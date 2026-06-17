package dev.rono.igniscore.block.pocketdimensioncache;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.StrategySupport;
import net.kyori.adventure.text.Component;

final class PocketDimensionCacheSupport {
    private PocketDimensionCacheSupport() {
    }

    static Component title(PocketDimensionCacheRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Pocket Cache") : definition.getTitle();
    
    }

    static int rows(PocketDimensionCacheRuntime runtime, BlockDefinition definition) {

        return Math.min(6, Math.max(1, StrategySupport.customInt(definition, "storageRows", 3)));
    
    }

    static IgnisWorld worldAt(PocketDimensionCacheRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

