package dev.rono.igniscore.block.chunkloaderlite;

import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class ChunkLoaderLiteSupport {
    private ChunkLoaderLiteSupport() {
    }

    static void tick(ChunkLoaderLiteRuntime runtime, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(runtime, location);
        IgnisLocation center = Locations.toCenter(location);
        var gui = runtime.registry.blockGui(location);
        boolean fueled = false;
        if (gui != null) {
            IgnisItem fuel = gui.inventory().getItem(0);
            fueled = fuel != null && !fuel.isAir() && isFuel(runtime, fuel.getMaterialKey());
            if (fueled && StrategySupport.customBoolean(definition, "consumeFuel", true)) {
                fuel.setAmount(fuel.getAmount() - 1);
                gui.inventory().setItem(0, fuel.getAmount() > 0 ? fuel : null);
            }
        }
        world.setChunkForceLoaded(location, fueled);
        if (fueled) {
            TheatricsSupport.pulseRing(world, center, 2.5, "PORTAL");
            world.playSound(center, "BLOCK_BEACON_AMBIENT", 0.2f, 1.5f);
        }
    
    }

    static boolean isFuel(ChunkLoaderLiteRuntime runtime, String material) {

        String key = material.toLowerCase();
        return key.contains("coal") || key.contains("charcoal") || key.contains("blaze_rod");
    
    }

    static Component title(ChunkLoaderLiteRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Chunk Loader Fuel") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(ChunkLoaderLiteRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

