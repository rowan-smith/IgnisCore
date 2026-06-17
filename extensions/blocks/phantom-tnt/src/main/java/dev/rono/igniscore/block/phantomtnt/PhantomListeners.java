package dev.rono.igniscore.block.phantomtnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class PhantomListeners implements OnBlockTickListener, OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    PhantomListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance) {
        if (instance.getTicksLeft() == ExplosionSupport.fuse(instance.getDefinition(), 160) - 20) {
            IgnisWorld world = worldAt(instance.getLocation());
            if (instance.getDisplayEntity() != null) {
                world.removeEntity(instance.getDisplayEntity());
                instance.setDisplayEntity(null);
            }
            IgnisLocation loc = Locations.toCenter(instance.getLocation());
            world.spawnParticle(loc, "SPORE_BLOSSOM_AIR", 20, 0.5, 0.5, 0.5, 0.05);
            world.playSound(loc, "ENTITY_PHANTOM_AMBIENT", 1.0f, 0.5f);
        }
    }

    void onTrigger(RuntimeBlockInstance instance) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        ExplosionSupport.createExplosion(worldAt(loc), loc, instance.getDefinition(), 4.0, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        onTick(event.instance());
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        onTrigger(event.instance());
    }
}
