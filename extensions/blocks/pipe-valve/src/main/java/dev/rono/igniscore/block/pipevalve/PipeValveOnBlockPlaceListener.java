package dev.rono.igniscore.block.pipevalve;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.util.Locations;

final class PipeValveOnBlockPlaceListener implements OnBlockPlaceListener {
    private final IgnisStrategyContext context;

    PipeValveOnBlockPlaceListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        String key = LinkedBlockRegistry.key(event.block().location());
        PipeValveSupport.OPEN.put(key, false);
        LinkedBlockRegistry.register(event.block().location(), (player, action) -> {
            if ("toggle".equals(action)) {
                boolean open = PipeValveSupport.OPEN.merge(key, false, (a, b) -> !a);
                IgnisWorld world = PipeValveSupport.worldAt(context, event.block().location());
                IgnisLocation center = Locations.toCenter(event.block().location());
                world.playSound(center, "BLOCK_IRON_DOOR_CLOSE", 0.7f, 0.9f);
                TheatricsSupport.sparkle(world, center, open ? "DRIPPING_WATER" : "LAVA", 8);
                player.sendMessage(open ? "<aqua>Valve open — flow enabled.</aqua>" : "<gray>Valve closed.</gray>");
            }
        });
        PlacedTickSupport.start(context, event.block().location(), 20L, () -> PipeValveSupport.tick(context, event.block().location()));
    }
}

