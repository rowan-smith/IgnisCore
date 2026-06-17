package dev.rono.igniscore.block.mobgrinderhub;

import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class MobGrinderHubSupport {
    private MobGrinderHubSupport() {
    }

    static void tick(IgnisStrategyContext ctx, BlockDefinition definition, IgnisLocation location) {

        IgnisWorld world = worldAt(ctx, location);
        IgnisLocation center = Locations.toCenter(location);
        double radius = StrategySupport.customDouble(definition, "grindRadius", 5.0);
          for (Object entity : world.getNearbyEntities(center, radius)) {
              if (!EntityUtilSupport.isHostile(entity)) {
                  continue;
              }
              world.setEntityVelocity(entity, 0, -0.6, 0);
              world.spawnParticle(world.getEntityLocation(entity), "DAMAGE_INDICATOR", 2, 0.1, 0.1, 0.1, 0.01);
          }
          if (EntityUtilSupport.countHostiles(world, center, radius) > 0) {
              world.playSound(center, "ENTITY_IRON_GOLEM_ATTACK", 0.5f, 1.3f);
          }
    
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

