package dev.rono.igniscore.block.spiderstormtnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class SpiderStormOnBlockTriggerListener implements OnBlockTriggerListener {
    private final SpiderStormRuntime runtime;

    SpiderStormOnBlockTriggerListener(SpiderStormRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = SpiderStormSupport.worldAt(runtime, loc);
        float finalPower = ExplosionSupport.resolvePower(def, 4.0);
        boolean realExplosion = StrategySupport.customBoolean(def, "realExplosion", true);

        if (realExplosion) {
            world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
            ExplosionSupport.createExplosion(world, loc, def, 4.0, false);
        } else {
            SpiderStormSupport.spawnBurst(runtime, world, loc, finalPower);
        }

        SpiderStormSupport.spawnEntityPayload(runtime, world, def, loc, finalPower);
    }
}

