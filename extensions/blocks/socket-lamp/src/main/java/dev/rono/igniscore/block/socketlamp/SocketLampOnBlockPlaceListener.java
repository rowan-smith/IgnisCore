package dev.rono.igniscore.block.socketlamp;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class SocketLampOnBlockPlaceListener implements OnBlockPlaceListener {
    private final IgnisStrategyContext context;

    SocketLampOnBlockPlaceListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        String key = LinkedBlockRegistry.key(event.block().location());
        SocketLampSupport.LIGHT_LEVEL.put(key, StrategySupport.customInt(event.block().definition(), "defaultLight", 15));
        LinkedBlockRegistry.register(event.block().location(), (player, action) -> {
            if ("cycle".equals(action)) {
                int level = SocketLampSupport.LIGHT_LEVEL.merge(key, 0, (a, b) -> (a + 1) % 16);
                IgnisWorld world = SocketLampSupport.worldAt(context, event.block().location());
                IgnisLocation center = Locations.toCenter(event.block().location());
                TheatricsSupport.sparkle(world, center, level > 0 ? "END_ROD" : "SMOKE", Math.max(1, level));
                world.playSound(center, "BLOCK_NOTE_BLOCK_PLING", 0.5f, 0.5f + level / 15f);
                player.sendActionBar("<yellow>Lamp level: <white>" + level + "</white>/15</yellow>");
            }
        });
        PlacedTickSupport.start(context, event.block().location(), 40L, () -> SocketLampSupport.tick(context, event.block().definition(), event.block().location()));
    }
}

