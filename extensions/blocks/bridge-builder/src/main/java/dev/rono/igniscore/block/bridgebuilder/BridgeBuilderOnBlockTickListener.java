package dev.rono.igniscore.block.bridgebuilder;

import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.igniscore.api.event.BlockTickEvent;
import dev.rono.igniscore.api.event.OnBlockTickListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.util.PlacedMetaSupport;

final class BridgeBuilderOnBlockTickListener implements OnBlockTickListener {
    private final IgnisStrategyContext context;

    BridgeBuilderOnBlockTickListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
        BlockDefinition def = event.instance().getDefinition();
        IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
        IgnisWorld world = BridgeBuilderSupport.worldAt(context, loc);
        int fuse = ExplosionSupport.fuseTicks(event.instance(), 80);
        int elapsed = ExplosionSupport.elapsedFuseTicks(event.instance(), 80);
        int interval = StrategySupport.customInt(def, "tickInterval", 5);
        if (elapsed % interval != 0) {
            return;
        }
        int length = StrategySupport.customInt(def, "bridgeLength", 6);
        float yaw = PlacedMetaSupport.placementYaw(event.instance().getLocation(), 0f);
        double dirX = -Math.sin(Math.toRadians(yaw));
        double dirZ = Math.cos(Math.toRadians(yaw));
        int step = elapsed / Math.max(1, interval);
        if (step > 0 && step <= length) {
            IgnisLocation block = Locations.toBlock(event.instance().getLocation()).add(dirX * step, 0, dirZ * step);
            world.setBlockMaterialKey(block, StrategySupport.customBoolean(def, "oakBridge", true) ? "oak_planks" : "stone");
            world.spawnParticle(block.add(0.5, 0.5, 0.5), "BLOCK", 2, 0.1, 0.1, 0.1, 0.01);
        }
    }
}

