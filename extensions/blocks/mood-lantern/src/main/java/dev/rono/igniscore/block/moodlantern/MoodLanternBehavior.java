package dev.rono.igniscore.block.moodlantern;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;

final class MoodLanternBehavior {
    private final IgnisStrategyContext context;

    MoodLanternBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        PlacedTickSupport.start(context, location, StrategySupport.customInt(definition, "tickPeriod", 25),
                () -> tick(definition, location));
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        PlacedTickSupport.stop(location);
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        double radius = StrategySupport.customDouble(definition, "moodRadius", 10.0);
        int hostiles = EntityUtilSupport.countHostiles(world, center, radius);
        int passives = EntityUtilSupport.countPassives(world, center, radius);
        int players = world.getPlayersNear(center, radius).size();
        int chaos = hostiles * 3 + players;
        int calm = passives + 1;
        String particle = chaos > calm ? "ANGRY_VILLAGER" : (players > 2 ? "NOTE" : "END_ROD");
        TheatricsSupport.sparkle(world, center.add(0, 1, 0), particle, StrategySupport.customInt(definition, "moodParticles", 3));
        world.playSound(center, "BLOCK_AMETHYST_BLOCK_CHIME", 0.3f, chaos > calm ? 0.7f : 1.4f);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
