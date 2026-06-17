package dev.rono.igniscore.block.silenttnt;

import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.PreviewTrickSupport;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.util.Locations;

final class SilentTntBehavior {
    private final IgnisStrategyContext context;

    SilentTntBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() % 20 != 0) {
            return;
        }
        IgnisLocation center = Locations.toCenter(instance.getLocation());
        worldAt(center).spawnParticle(center, "SMOKE", 2, 0.2, 0.2, 0.2, 0.01);
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        float realPower = (float) StrategySupport.customDouble(instance.getDefinition(), "realPower", 4.0);
        boolean blockDamage = StrategySupport.customBoolean(instance.getDefinition(), "blockDamage", true);
        PreviewTrickSupport.silentDetonation(world, loc, realPower, blockDamage);
        PreviewTrickSupport.forNearbyPlayers(world, loc, 32, player ->
                context.effects().playFakeExplosion(loc, 0f, world.getPlayersNear(loc, 32)));
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
