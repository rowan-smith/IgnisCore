package dev.rono.igniscore.block.socketlamp;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class SocketLampListeners implements OnBlockPlaceListener, OnBlockBreakListener {
    private static final Map<String, Integer> LIGHT_LEVEL = new ConcurrentHashMap<>();

    private final IgnisStrategyContext context;

    SocketLampListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        int level = LIGHT_LEVEL.getOrDefault(LinkedBlockRegistry.key(location), 0);
        if (level <= 0) {
            return;
        }
        IgnisWorld world = worldAt(location);
        world.spawnParticle(Locations.toCenter(location), "END_ROD", Math.min(8, level / 2),
                0.3, 0.2, 0.3, 0.01);
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                String key = LinkedBlockRegistry.key(event.block().location());
                LIGHT_LEVEL.put(key, StrategySupport.customInt(event.block().definition(), "defaultLight", 15));
                LinkedBlockRegistry.register(event.block().location(), (player, action) -> {
                    if ("cycle".equals(action)) {
                        int level = LIGHT_LEVEL.merge(key, 0, (a, b) -> (a + 1) % 16);
                        IgnisWorld world = worldAt(event.block().location());
                        IgnisLocation center = Locations.toCenter(event.block().location());
                        TheatricsSupport.sparkle(world, center, level > 0 ? "END_ROD" : "SMOKE", Math.max(1, level));
                        world.playSound(center, "BLOCK_NOTE_BLOCK_PLING", 0.5f, 0.5f + level / 15f);
                        player.sendActionBar("<yellow>Lamp level: <white>" + level + "</white>/15</yellow>");
                    }
                });
                PlacedTickSupport.start(context, event.block().location(), 40L, () -> tick(event.block().definition(), event.block().location()));
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                PlacedTickSupport.stop(event.block().location());
                LinkedBlockRegistry.unregister(event.block().location());
                LIGHT_LEVEL.remove(LinkedBlockRegistry.key(event.block().location()));
    }
}
