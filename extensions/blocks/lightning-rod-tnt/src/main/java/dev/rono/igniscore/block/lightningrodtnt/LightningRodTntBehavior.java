package dev.rono.igniscore.block.lightningrodtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class LightningRodTntBehavior {
    private final IgnisStrategyContext context;

    LightningRodTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 10 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        worldAt(center).spawnParticle(center, "ELECTRIC_SPARK", 6, 0.4, 0.6, 0.4, 0.1);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        double chainRadius = StrategySupport.customDouble(def, "chainRadius", 8.0);
        world.playSound(loc, "ENTITY_LIGHTNING_BOLT_THUNDER", 2.5f, 0.8f);
        world.playSound(loc, "ENTITY_LIGHTNING_BOLT_IMPACT", 2.0f, 1.0f);
        world.spawnParticle(loc, "ELECTRIC_SPARK", 50, 1, 2, 1, 0.2);
        world.spawnParticle(loc, "FLASH", 2, 0, 0, 0, 0);
        for (IgnisPlayer player : world.getPlayersNear(loc, chainRadius)) {
            player.applyPotionEffect("INSTANT_DAMAGE", 1, 1);
            player.getWorld().playSound(player.getLocation(), "ENTITY_LIGHTNING_BOLT_IMPACT", 1.5f, 1.2f);
        }
        for (Object entity : world.getNearbyEntities(loc, chainRadius)) {
            if (entity instanceof IgnisPlayer) {
                continue;
            }
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc != null) {
                world.spawnParticle(entityLoc, "ELECTRIC_SPARK", 8, 0.2, 0.5, 0.2, 0.1);
            }
        }
        ExplosionSupport.createExplosion(world, loc, def, 3.5, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
