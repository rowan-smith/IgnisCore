package dev.rono.igniscore.block.blinktnt;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class BlinkTntOnBlockTickListener implements OnBlockTickListener {
    private final IgnisStrategyContext context;

    BlinkTntOnBlockTickListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = BlinkTntSupport.worldAt(context, loc);
        int fuse = ExplosionSupport.fuseTicks(event.instance(), 80);
        int elapsed = ExplosionSupport.elapsedFuseTicks(event.instance(), 80);
        int interval = StrategySupport.customInt(def, "tickInterval", 5);
        if (elapsed % interval != 0) {
            return;
        }
        if (elapsed % StrategySupport.customInt(def, "blinkInterval", 14) == 0) {
            double range = StrategySupport.customDouble(def, "blinkRange", 1.5);
            double angle = Math.random() * Math.PI * 2;
            IgnisLocation blink = loc.add(Math.cos(angle) * range, 0, Math.sin(angle) * range);
            world.spawnParticle(loc, "PORTAL", 8, 0.2, 0.4, 0.2, 0.05);
            world.spawnParticle(blink, "REVERSE_PORTAL", 8, 0.2, 0.4, 0.2, 0.05);
            world.playSound(loc, "ENTITY_ENDERMAN_TELEPORT", 0.5f, 1.4f);
        }
    }
}

