package dev.rono.igniscore.block.signalcharge;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class SignalChargeListeners implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    SignalChargeListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTrigger(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 0.9f);
        ExplosionSupport.createExplosion(world, loc, def, 4.0, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        onTrigger(event.instance());
    }
}
