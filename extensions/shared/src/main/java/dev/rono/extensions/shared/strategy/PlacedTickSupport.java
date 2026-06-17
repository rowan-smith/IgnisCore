package dev.rono.extensions.shared.strategy;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisTask;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repeating tick tasks for placed (non-active) custom blocks.
 */
public final class PlacedTickSupport {
    private static final Map<String, IgnisTask> ACTIVE = new ConcurrentHashMap<>();

    private PlacedTickSupport() {
    }

    public static void start(IgnisStrategyContext context, IgnisLocation location, long periodTicks, Runnable action) {
        stop(location);
        IgnisTask[] ref = {null};
        ref[0] = context.scheduler().runRepeating(location, action, periodTicks, periodTicks);
        ACTIVE.put(key(location), ref[0]);
    }

    public static void stop(IgnisLocation location) {
        IgnisTask task = ACTIVE.remove(key(location));
        if (task != null) {
            task.cancel();
        }
    }

    private static String key(IgnisLocation location) {
        IgnisLocation block = Locations.toBlock(location);
        UUID worldId = block.worldId();
        String worldName = block.worldName() == null ? "world" : block.worldName();
        UUID resolved = worldId != null ? worldId : UUID.nameUUIDFromBytes(worldName.getBytes());
        return resolved + ":" + worldName + ":"
                + (int) Math.floor(block.x()) + ":"
                + (int) Math.floor(block.y()) + ":"
                + (int) Math.floor(block.z());
    }
}
