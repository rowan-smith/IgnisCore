package dev.rono.igniscore.block.scarecrowanchor;

import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class ScarecrowAnchorSupport {
    private ScarecrowAnchorSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
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

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

