package dev.rono.igniscore.block.tunnelingtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class TunnelingBehavior {
    private final IgnisStrategyContext context;

    TunnelingBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);

        double dirX = 0;
        double dirZ = 1;
        IgnisPlayer player = context.getExtensionSupport().wrapPlayer(triggerContext);
        if (player != null) {
            IgnisLocation eye = player.getEyeLocation();
            double yawRad = Math.toRadians(eye.yaw());
            dirX = -Math.sin(yawRad);
            dirZ = Math.cos(yawRad);
        }
        double length = Math.hypot(dirX, dirZ);
        if (length > 0) {
            dirX /= length;
            dirZ /= length;
        }
        final double directionX = dirX;
        final double directionZ = dirZ;

        BlockDefinition def = instance.getDefinition();
        int tunnelLength = StrategySupport.customInt(def, "tunnelLength", 16);
        double tunnelGap = StrategySupport.customDouble(def, "tunnelGap", 2.0);

        double[] current = {loc.x(), loc.y(), loc.z()};
        int[] count = {0};
        IgnisTask[] taskRef = {null};
        taskRef[0] = context.getScheduler().runRepeating(loc, () -> {
            if (count[0]++ >= tunnelLength) {
                if (taskRef[0] != null) {
                    taskRef[0].cancel();
                }
                return;
            }
            current[0] += directionX * tunnelGap;
            current[2] += directionZ * tunnelGap;
            IgnisLocation currentLoc = new IgnisLocation(loc.worldId(), loc.worldName(), current[0], current[1], current[2], 0f, 0f);
            StrategySupport.createExplosion(world, currentLoc, 4.0f, false, true);
            world.spawnParticle(currentLoc, "SMOKE", 20, 0.5, 0.5, 0.5, 0.05);
        }, 0L, 4L);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
