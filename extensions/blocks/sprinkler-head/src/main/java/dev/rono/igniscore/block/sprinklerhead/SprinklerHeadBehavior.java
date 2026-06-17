package dev.rono.igniscore.block.sprinklerhead;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.util.Locations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class SprinklerHeadBehavior {
    private static final Map<String, Boolean> ARMED = new ConcurrentHashMap<>();

    private final IgnisStrategyContext context;

    SprinklerHeadBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        String key = LinkedBlockRegistry.key(location);
        ARMED.put(key, false);
        LinkedBlockRegistry.register(location, (player, action) -> {
            if ("arm".equals(action) || "toggle".equals(action)) {
                boolean armed = ARMED.merge(key, false, (a, b) -> !a);
                player.sendMessage(armed ? "<green>Sprinkler armed.</green>" : "<gray>Sprinkler disarmed.</gray>");
                IgnisWorld world = worldAt(location);
                world.playSound(Locations.toCenter(location), "BLOCK_DISPENSER_DISPENSE", 0.6f, 1.0f);
            }
        });
        PlacedTickSupport.start(context, location, StrategySupport.customInt(definition, "tickPeriod", 60),
                () -> tick(definition, location));
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        PlacedTickSupport.stop(location);
        LinkedBlockRegistry.unregister(location);
        ARMED.remove(LinkedBlockRegistry.key(location));
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        if (!ARMED.getOrDefault(LinkedBlockRegistry.key(location), false)) {
            return;
        }
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        int radius = StrategySupport.customInt(definition, "waterRadius", 4);
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (x * x + z * z > radius * radius) {
                    continue;
                }
                IgnisLocation soil = center.add(x, -1, z);
                if (world.getBlockMaterialKey(soil).toLowerCase().contains("farmland")) {
                    world.spawnParticle(soil.add(0.5, 1, 0.5), "FALLING_WATER", 2, 0.1, 0.1, 0.1, 0.01);
                }
            }
        }
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
