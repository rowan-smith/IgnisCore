package dev.rono.igniscore.block.pipevalve;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class PipeValveBehavior {
    private static final Map<String, Boolean> OPEN = new ConcurrentHashMap<>();

    private final IgnisStrategyContext context;

    PipeValveBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        String key = LinkedBlockRegistry.key(location);
        OPEN.put(key, false);
        LinkedBlockRegistry.register(location, (player, action) -> {
            if ("toggle".equals(action)) {
                boolean open = OPEN.merge(key, false, (a, b) -> !a);
                IgnisWorld world = worldAt(location);
                IgnisLocation center = Locations.toCenter(location);
                world.playSound(center, "BLOCK_IRON_DOOR_CLOSE", 0.7f, 0.9f);
                TheatricsSupport.sparkle(world, center, open ? "DRIPPING_WATER" : "LAVA", 8);
                player.sendMessage(open ? "<aqua>Valve open — flow enabled.</aqua>" : "<gray>Valve closed.</gray>");
            }
        });
        PlacedTickSupport.start(context, location, 20L, () -> tick(location));
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        PlacedTickSupport.stop(location);
        LinkedBlockRegistry.unregister(location);
        OPEN.remove(LinkedBlockRegistry.key(location));
    }

    private void tick(IgnisLocation location) {
        if (!OPEN.getOrDefault(LinkedBlockRegistry.key(location), false)) {
            return;
        }
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        world.spawnParticle(center, "DRIPPING_WATER", 4, 0.2, 0.1, 0.2, 0.01);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
