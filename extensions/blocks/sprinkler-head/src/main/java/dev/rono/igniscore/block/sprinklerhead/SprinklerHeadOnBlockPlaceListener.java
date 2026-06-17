package dev.rono.igniscore.block.sprinklerhead;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;

final class SprinklerHeadOnBlockPlaceListener implements OnBlockPlaceListener {
    private final IgnisStrategyContext context;

    SprinklerHeadOnBlockPlaceListener(IgnisStrategyContext context) {
        this.context = context;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        String key = LinkedBlockRegistry.key(event.block().location());
        SprinklerHeadSupport.ARMED.put(key, false);
        LinkedBlockRegistry.register(event.block().location(), (player, action) -> {
            if ("arm".equals(action) || "toggle".equals(action)) {
                boolean armed = SprinklerHeadSupport.ARMED.merge(key, false, (a, b) -> !a);
                player.sendMessage(armed ? "<green>Sprinkler armed.</green>" : "<gray>Sprinkler disarmed.</gray>");
                IgnisWorld world = SprinklerHeadSupport.worldAt(context, event.block().location());
                world.playSound(Locations.toCenter(event.block().location()), "BLOCK_DISPENSER_DISPENSE", 0.6f, 1.0f);
            }
        });
        PlacedTickSupport.start(context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 60),
                () -> SprinklerHeadSupport.tick(context, event.block().definition(), event.block().location()));
    }
}

