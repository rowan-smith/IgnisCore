package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proximity arming for buried mines placed in the world.
 */
public final class BuriedMineSupport {
    private static final Map<String, IgnisTask> ACTIVE_WATCHERS = new ConcurrentHashMap<>();

    private BuriedMineSupport() {
    }

    public static void arm(IgnisStrategyContext context,
                            IgnisLocation blockLocation,
                            String blockTypeId,
                            double triggerRadius) {
        String key = encode(blockLocation);
        disarm(blockLocation);

        IgnisTask[] taskRef = {null};
        taskRef[0] = context.scheduler().runRepeating(blockLocation, () -> {
            if (!blockTypeId.equalsIgnoreCase(IgnisCoreAPI.getPlacedBlockType(Locations.toBlock(blockLocation)))) {
                disarm(blockLocation);
                return;
            }

            IgnisWorld world = context.extensions().resolveWorld(blockLocation);
            IgnisLocation center = Locations.toCenter(blockLocation);
            boolean triggered = false;

            for (Object entity : world.getNearbyEntities(center, triggerRadius)) {
                IgnisLocation entityLoc = world.getEntityLocation(entity);
                if (entityLoc == null) {
                    continue;
                }
                if (Math.abs(entityLoc.x() - center.x()) <= triggerRadius
                        && Math.abs(entityLoc.z() - center.z()) <= triggerRadius
                        && entityLoc.y() >= blockLocation.y() - 0.2
                        && entityLoc.y() <= blockLocation.y() + 1.8) {
                    triggered = true;
                    break;
                }
            }

            if (triggered) {
                disarm(blockLocation);
                world.spawnParticle(center, "SMOKE", 6, 0.2, 0.05, 0.2, 0.01);
                IgnisCoreAPI.ignitePlacedBlock(Locations.toBlock(blockLocation), null);
            }
        }, 10L, 5L);

        ACTIVE_WATCHERS.put(key, taskRef[0]);
    }

    public static void disarm(IgnisLocation blockLocation) {
        IgnisTask task = ACTIVE_WATCHERS.remove(encode(blockLocation));
        if (task != null) {
            task.cancel();
        }
    }

    private static String encode(IgnisLocation location) {
        UUID worldId = location.worldId();
        String worldName = location.worldName() == null ? "world" : location.worldName();
        UUID resolved = worldId != null ? worldId : UUID.nameUUIDFromBytes(worldName.getBytes());
        return resolved + ":" + worldName + ":"
                + (int) Math.floor(location.x()) + ":"
                + (int) Math.floor(location.y()) + ":"
                + (int) Math.floor(location.z());
    }
}
