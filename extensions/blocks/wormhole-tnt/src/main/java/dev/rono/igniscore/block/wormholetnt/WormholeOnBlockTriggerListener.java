package dev.rono.igniscore.block.wormholetnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class WormholeOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    WormholeOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = WormholeSupport.worldAt(context, loc);
        BlockDefinition def = event.instance().getDefinition();

        ExplosionSupport.createExplosion(world, loc, def, 10.0, false);
        world.spawnParticle(loc, "EXPLOSION_EMITTER", 5, 2, 2, 2, 0);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 2.0f, 0.5f);
    }
}

