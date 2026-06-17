package dev.rono.igniscore.block.solarflaretnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.util.Locations;

final class SolarFlareTntBehavior {
    private final IgnisStrategyContext context;

    SolarFlareTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 8 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        worldAt(center).spawnParticle(center, "END_ROD", 6, 0.5, 0.8, 0.5, 0.05);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        double blindRadius = StrategySupport.customDouble(def, "blindRadius", 16.0);
        int blindDuration = StrategySupport.customInt(def, "blindDuration", 80);
        world.playSound(loc, "ENTITY_BLAZE_SHOOT", 2.0f, 0.4f);
        world.spawnParticle(loc, "FLASH", 3, 0, 0, 0, 0);
        world.spawnParticle(loc, "END_ROD", 50, 2, 2, 2, 0.1);
        for (IgnisPlayer player : world.getPlayersNear(loc, blindRadius)) {
            player.applyPotionEffect("GLOWING", blindDuration, 0);
            player.applyPotionEffect("BLINDNESS", blindDuration / 2, 0);
            player.applyPotionEffect("INSTANT_DAMAGE", 1, 1);
        }
        ExplosionSupport.createExplosion(world, loc, def, 3.5, true);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
