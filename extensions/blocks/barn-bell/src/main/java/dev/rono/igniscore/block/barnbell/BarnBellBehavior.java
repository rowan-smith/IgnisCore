package dev.rono.igniscore.block.barnbell;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.igniscore.api.util.Locations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class BarnBellBehavior {
    private static final Map<String, Long> COOLDOWN = new ConcurrentHashMap<>();

    private final IgnisStrategyContext context;

    BarnBellBehavior(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        String key = LinkedBlockRegistry.key(location);
        LinkedBlockRegistry.register(location, (player, action) -> {
            if (!"call".equals(action)) {
                return;
            }
            long now = System.currentTimeMillis();
            long cooldownMs = StrategySupport.customInt(definition, "callCooldownTicks", 200) * 50L;
            Long last = COOLDOWN.get(key);
            if (last != null && now - last < cooldownMs) {
                player.sendMessage("<red>Barn bell on cooldown.</red>");
                return;
            }
            COOLDOWN.put(key, now);
            IgnisWorld world = worldAt(location);
            IgnisLocation center = Locations.toCenter(location);
            double radius = StrategySupport.customDouble(definition, "herdRadius", 24.0);
            EntityUtilSupport.herdPassives(world, center, radius);
            world.playSound(center, "BLOCK_BELL_USE", 1.0f, 0.8f);
            world.spawnParticle(center, "NOTE", 12, 0.5, 0.5, 0.5, 0.1);
            player.sendMessage("<gold>Barn bell calls livestock.</gold>");
        });
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        LinkedBlockRegistry.unregister(location);
        COOLDOWN.remove(LinkedBlockRegistry.key(location));
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
