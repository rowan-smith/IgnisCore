package dev.rono.igniscore.block.irrigationsprinkler;

import dev.rono.extensions.shared.strategy.ProcessingGuiSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

final class IrrigationSprinklerSupport {
    private IrrigationSprinklerSupport() {
    }

    static final int WATER_SLOT = 13;

    static void tick(IrrigationSprinklerRuntime runtime, BlockDefinition definition, IgnisLocation location) {

        var gui = runtime.registry.blockGui(location);
        if (gui == null) {
            return;
        }
        IgnisItem water = gui.inventory().getItem(WATER_SLOT);
        if (!ProcessingGuiSupport.matches(water, "water_bucket", "bucket")) {
            return;
        }
        IgnisWorld world = worldAt(runtime, location);
        IgnisLocation center = Locations.toCenter(location);
        int radius = StrategySupport.customInt(definition, "waterRadius", 4);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) {
                    continue;
                }
                IgnisLocation soil = center.add(x, -1, z);
                String material = world.getBlockMaterialKey(soil).toLowerCase();
                if (material.contains("farmland")) {
                    world.spawnParticle(soil.add(0.5, 1, 0.5), "FALLING_WATER", 2, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }
        ProcessingGuiSupport.consumeOne(gui.inventory(), WATER_SLOT);
        world.playSound(center, "BLOCK_WATER_AMBIENT", 0.4f, 1.0f);
    
    }

    static Component title(IrrigationSprinklerRuntime runtime, BlockDefinition definition) {

        return definition.getTitle() == null ? Component.text("Sprinkler Tank") : definition.getTitle();
    
    }

    static IgnisWorld worldAt(IrrigationSprinklerRuntime runtime, IgnisLocation location) {

        return runtime.context.extensions().resolveWorld(location);
    
    }
}

