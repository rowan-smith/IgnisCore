package dev.rono.igniscore.block.riftgenerator;

import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class RiftGeneratorOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    RiftGeneratorOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = RiftGeneratorSupport.worldAt(context, loc);
        double radius = StrategySupport.customDouble(def, "riftRadius", 7.0);
        EntityUtilSupport.teleportRandomHorizontal(world, loc, radius, 3.0);
        world.playSound(loc, "ENTITY_ENDER_DRAGON_GROWL", 0.8f, 1.2f);
        ExplosionSupport.createExplosion(world, loc, def, 4.5, false);
    }
}

