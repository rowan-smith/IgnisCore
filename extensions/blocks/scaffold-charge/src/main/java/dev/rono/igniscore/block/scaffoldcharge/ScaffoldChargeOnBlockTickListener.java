package dev.rono.igniscore.block.scaffoldcharge;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class ScaffoldChargeOnBlockTickListener implements OnBlockTickListener {
    private final IgnisStrategyContext context;

    ScaffoldChargeOnBlockTickListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = ScaffoldChargeSupport.worldAt(context, loc);
        int fuse = ExplosionSupport.fuseTicks(event.instance(), 80);
        int elapsed = ExplosionSupport.elapsedFuseTicks(event.instance(), 80);
        int interval = StrategySupport.customInt(def, "tickInterval", 5);
        if (elapsed % interval != 0) {
            return;
        }
        if (elapsed % 10 == 0) {
            int height = StrategySupport.customInt(def, "scaffoldHeight", 4);
            for (int y = 0; y < height; y++) {
                IgnisLocation pillar = Locations.toBlock(event.instance().getLocation()).add(0, y, 0);
                world.setBlockMaterialKey(pillar, "scaffolding");
                world.spawnParticle(pillar.add(0.5, 0.5, 0.5), "CRIT", 1, 0, 0, 0, 0);
            }
        }
    }
}

