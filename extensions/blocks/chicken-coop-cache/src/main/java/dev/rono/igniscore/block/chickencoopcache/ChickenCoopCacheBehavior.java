package dev.rono.igniscore.block.chickencoopcache;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;

final class ChickenCoopCacheBehavior {
    private final IgnisStrategyContext context;

    ChickenCoopCacheBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        long period = StrategySupport.customInt(definition, "tickPeriod", 20);
        PlacedTickSupport.start(context, location, period, () -> tick(definition, location));
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.chime(worldAt(center), center, 1.0f);
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        PlacedTickSupport.stop(location);
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.sparkle(world, center, "EGG_CRACK", StrategySupport.customInt(definition, "eggParticles", 3));
          world.playSound(center, "ENTITY_CHICKEN_EGG", 0.4f, 1.2f);
          if (StrategySupport.customBoolean(definition, "collectSimulation", true)) {
              playerMessageNearby(world, center, "<gray>Coop collected <yellow>1 egg</yellow>.</gray>");
          }
    }

    private void playerMessageNearby(IgnisWorld world, IgnisLocation center, String message) {
        for (IgnisPlayer player : world.getPlayersNear(center, 6.0)) {
            player.sendActionBar(message);
        }
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
