package dev.rono.igniscore.block.keyedhatch;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
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

final class KeyedHatchListeners implements OnBlockPlaceListener, OnBlockBreakListener {
    private static final Map<String, Boolean> OPEN = new ConcurrentHashMap<>();

    private final IgnisStrategyContext context;

    KeyedHatchListeners(IgnisStrategyContext context) {
        this.context = context;
    }

    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        String key = LinkedBlockRegistry.key(location);
        OPEN.put(key, false);
        LinkedBlockRegistry.register(location, (player, action) -> {
            if ("toggle".equals(action)) {
                boolean open = OPEN.merge(key, false, (a, b) -> !a);
                IgnisWorld world = worldAt(location);
                IgnisLocation block = Locations.toBlock(location);
                world.setBlockMaterialKey(block, open ? "iron_trapdoor" : "iron_bars");
                TheatricsSupport.sparkle(world, block.add(0.5, 0.5, 0.5), "CRIT", 6);
                world.playSound(block, "BLOCK_IRON_TRAPDOOR_OPEN", 0.8f, open ? 1.0f : 0.8f);
                player.sendMessage(open ? "<gray>Hatch opened.</gray>" : "<gray>Hatch closed.</gray>");
            }
        });
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        LinkedBlockRegistry.unregister(location);
        OPEN.remove(LinkedBlockRegistry.key(location));
    }

    private IgnisWorld worldAt(IgnisLocation location) {
        return context.extensions().resolveWorld(location);
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        onPlaced(event.block().definition(), event.block().location());
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        onPlacedBreak(event.block().definition(), event.block().location());
    }
}
