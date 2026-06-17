package dev.rono.igniscore.block.pipevalve;

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
import dev.rono.igniscore.api.util.Locations;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class PipeValveListeners implements OnBlockPlaceListener, OnBlockBreakListener {
    private static final Map<String, Boolean> OPEN = new ConcurrentHashMap<>();

    private final IgnisStrategyContext context;

    PipeValveListeners(IgnisStrategyContext context) {
        this.context = context;
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
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
                String key = LinkedBlockRegistry.key(event.block().location());
                OPEN.put(key, false);
                LinkedBlockRegistry.register(event.block().location(), (player, action) -> {
                    if ("toggle".equals(action)) {
                        boolean open = OPEN.merge(key, false, (a, b) -> !a);
                        IgnisWorld world = worldAt(event.block().location());
                        IgnisLocation center = Locations.toCenter(event.block().location());
                        world.playSound(center, "BLOCK_IRON_DOOR_CLOSE", 0.7f, 0.9f);
                        TheatricsSupport.sparkle(world, center, open ? "DRIPPING_WATER" : "LAVA", 8);
                        player.sendMessage(open ? "<aqua>Valve open — flow enabled.</aqua>" : "<gray>Valve closed.</gray>");
                    }
                });
                PlacedTickSupport.start(context, event.block().location(), 20L, () -> tick(event.block().location()));
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                PlacedTickSupport.stop(event.block().location());
                LinkedBlockRegistry.unregister(event.block().location());
                OPEN.remove(LinkedBlockRegistry.key(event.block().location()));
    }
}
