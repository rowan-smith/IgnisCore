package dev.rono.igniscore.block.bridgebuilder;

import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.ExplosionVariantsSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
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
import dev.rono.igniscore.api.util.PlacedMetaSupport;

final class BridgeBuilderListeners implements OnBlockTickListener, OnBlockTriggerListener {
    private final IgnisStrategyContext context;

    BridgeBuilderListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockTick(BlockTickEvent event) {
                BlockDefinition def = event.instance().getDefinition();
                IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
                IgnisWorld world = worldAt(loc);
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

    @Override
    public void onBlockTrigger(BlockTriggerEvent event) {
                BlockDefinition def = event.instance().getDefinition();
                IgnisLocation loc = Locations.toCenter(event.instance().getLocation());
                IgnisWorld world = worldAt(loc);
                world.playSound(loc, "BLOCK_WOOD_PLACE", 1.0f, 0.7f);
                ExplosionSupport.createExplosion(world, loc, def, 2.5, false);
    }
}
