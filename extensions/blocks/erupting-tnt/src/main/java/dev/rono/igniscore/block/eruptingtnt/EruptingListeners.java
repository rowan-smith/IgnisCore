package dev.rono.igniscore.block.eruptingtnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class EruptingListeners implements OnBlockTickListener, OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    EruptingListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTick(RuntimeBlockInstance instance, BlockDefinition def) {
        int interval = StrategySupport.customInt(def, "eruptionInterval", 5);

        if (instance.getTicksLeft() % interval == 0 && instance.getTicksLeft() < ExplosionSupport.fuse(def, 100) - 10) {
            IgnisLocation loc = Locations.toCenter(instance.getLocation());
            IgnisWorld world = worldAt(loc);
            Object tnt = world.spawnEntity("TNT", loc);
            int eruptionFuse = StrategySupport.customInt(def, "eruptionFuse", 80);
            world.configurePrimedTnt(tnt, eruptionFuse, 4.0f, false);

            double horizontalPower = StrategySupport.customDouble(def, "eruptionHorizontalPower", 0.4);
            double verticalPower = StrategySupport.customDouble(def, "eruptionVerticalPower", 1.2);
            world.setEntityVelocity(
                    tnt,
                    (Math.random() - 0.5) * horizontalPower,
                    verticalPower,
                    (Math.random() - 0.5) * horizontalPower);

            world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 0.5f, 1.5f);
            world.spawnParticle(loc, "EXPLOSION_EMITTER", 1, 0, 0, 0, 0);
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
        onTick(event.instance(), event.definition());
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        onTrigger(event.instance());
    }
}
