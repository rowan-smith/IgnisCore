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
                        IgnisLocation block = Locations.toBlock(event.block().location());
                        world.setBlockMaterialKey(block, open ? "iron_trapdoor" : "iron_bars");
                        TheatricsSupport.sparkle(world, block.add(0.5, 0.5, 0.5), "CRIT", 6);
                        world.playSound(block, "BLOCK_IRON_TRAPDOOR_OPEN", 0.8f, open ? 1.0f : 0.8f);
                        player.sendMessage(open ? "<gray>Hatch opened.</gray>" : "<gray>Hatch closed.</gray>");
                    }
                });
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
                LinkedBlockRegistry.unregister(event.block().location());
                OPEN.remove(LinkedBlockRegistry.key(event.block().location()));
    }
}
