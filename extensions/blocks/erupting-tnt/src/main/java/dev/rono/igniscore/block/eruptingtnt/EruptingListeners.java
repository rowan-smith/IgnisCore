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

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
                int interval = StrategySupport.customInt(event.definition(), "eruptionInterval", 5);

                if (event.instance().getTicksLeft() % interval == 0 && event.instance().getTicksLeft() < ExplosionSupport.fuse(event.definition(), 100) - 10) {
                    IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
                    IgnisWorld world = worldAt(loc);
                    Object tnt = world.spawnEntity("TNT", loc);
                    int eruptionFuse = StrategySupport.customInt(event.definition(), "eruptionFuse", 80);
                    world.configurePrimedTnt(tnt, eruptionFuse, 4.0f, false);

                    double horizontalPower = StrategySupport.customDouble(event.definition(), "eruptionHorizontalPower", 0.4);
                    double verticalPower = StrategySupport.customDouble(event.definition(), "eruptionVerticalPower", 1.2);
                    world.setEntityVelocity(
                            tnt,
                            (Math.random() - 0.5) * horizontalPower,
                            verticalPower,
                            (Math.random() - 0.5) * horizontalPower);

                    world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 0.5f, 1.5f);
                    world.spawnParticle(loc, "EXPLOSION_EMITTER", 1, 0, 0, 0, 0);
                }
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
                IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
                ExplosionSupport.createExplosion(worldAt(loc), loc, event.instance().getDefinition(), 4.0, false);
    }
}
