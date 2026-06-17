package dev.rono.igniscore.block.invisiwall;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class InvisiwallBehavior {
    private final IgnisStrategyContext context;

    InvisiwallBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
        int width = StrategySupport.customInt(definition, "wallWidth", 3);
        int height = StrategySupport.customInt(definition, "wallHeight", 3);
        float yaw = (float) StrategySupport.customDouble(definition, "wallYaw", 0);
        double yawRad = Math.toRadians(yaw);
        double nx = -Math.sin(yawRad);
        double nz = Math.cos(yawRad);
        for (int w = 0; w < width; w++) {
            for (int h = 0; h < height; h++) {
                IgnisLocation panel = location.add(nx * w, h, nz * w);
                for (IgnisPlayer player : world.getPlayersNear(Locations.toCenter(location), 24)) {
                    context.effects().showBlockPreview(player, panel, "glass");
                }
            }
        }
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        world.playSound(loc, "BLOCK_GLASS_BREAK", 1.5f, 0.6f);
        world.spawnParticle(loc, "BLOCK", 30, 1.5, 1, 1.5, 0.1);
        world.createExplosion(loc, 2.0f, false, false);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }
}
