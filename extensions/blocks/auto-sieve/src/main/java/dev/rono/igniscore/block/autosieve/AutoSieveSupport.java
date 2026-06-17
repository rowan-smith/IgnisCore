package dev.rono.igniscore.block.autosieve;

import dev.rono.igniscore.api.model.PlacedBlock;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;

final class AutoSieveSupport {
    private AutoSieveSupport() {
    }

    static void tick(IgnisStrategyContext context, PlacedBlock block) {
        IgnisWorld world = worldAt(context, block.location());
        IgnisLocation center = Locations.toCenter(block.location());
        TheatricsSupport.sparkle(world, center, "BLOCK", StrategySupport.customInt(block.definition(), "sieveParticles", 6));
        world.playSound(center, "BLOCK_SAND_BREAK", 0.4f, 1.3f);
    }

    static IgnisWorld worldAt(IgnisStrategyContext context, IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
