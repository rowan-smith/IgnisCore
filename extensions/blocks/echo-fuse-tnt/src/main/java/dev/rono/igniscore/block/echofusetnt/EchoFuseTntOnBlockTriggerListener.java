package dev.rono.igniscore.block.echofusetnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTriggerEvent;
import dev.rono.igniscore.api.event.OnBlockTriggerListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class EchoFuseTntOnBlockTriggerListener implements OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    EchoFuseTntOnBlockTriggerListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = EchoFuseTntSupport.worldAt(context, loc);
        float power = ExplosionSupport.resolvePower(def, 4.0);
        int echoes = StrategySupport.customInt(def, "echoBursts", 3);
        int delay = StrategySupport.customInt(def, "echoDelay", 10);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
        ExplosionSupport.createExplosion(world, loc, def, power, false);
        for (int i = 1; i <= echoes; i++) {
            final int echo = i;
            context.scheduler().runLater(loc, () -> {
                world.playSound(loc, "ENTITY_FIREWORK_ROCKET_BLAST", 0.9f, 0.8f + echo * 0.1f);
                world.spawnParticle(loc, "CLOUD", 12, 1.0, 0.5, 1.0, 0.04);
                ExplosionSupport.createExplosion(world, loc, power * 0.45f, false, false);
            }, delay * (long) echo);
        }
    }
}

