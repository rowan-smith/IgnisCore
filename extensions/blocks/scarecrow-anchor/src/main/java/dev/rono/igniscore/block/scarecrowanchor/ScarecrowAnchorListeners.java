package dev.rono.igniscore.block.scarecrowanchor;

import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class ScarecrowAnchorListeners implements OnBlockPlaceListener, OnBlockBreakListener {
    private final IgnisStrategyContext context;

    ScarecrowAnchorListeners(IgnisStrategyContext context) {
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
        double radius = StrategySupport.customDouble(definition, "scareRadius", 8.0);
          for (Object entity : world.getNearbyEntities(center, radius)) {
              if (!EntityUtilSupport.isHostile(entity)) {
                  continue;
              }
              IgnisLocation entityLoc = world.getEntityLocation(entity);
              if (entityLoc == null) {
                  continue;
              }
              double dx = entityLoc.x() - center.x();
              double dz = entityLoc.z() - center.z();
              double dist = Math.max(0.5, Math.sqrt(dx * dx + dz * dz));
              world.setEntityVelocity(entity, dx / dist * 0.5, 0.1, dz / dist * 0.5);
          }
          world.spawnParticle(center.add(0, 1.5, 0), "BLOCK", 3, 0.2, 0.2, 0.2, 0.01);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        onPlaced(event.block().definition(), event.block().location());
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        onPlacedBreak(event.block().definition(), event.block().location());
    }
}
