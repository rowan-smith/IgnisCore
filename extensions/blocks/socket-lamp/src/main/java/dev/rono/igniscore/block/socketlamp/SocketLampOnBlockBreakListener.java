package dev.rono.igniscore.block.socketlamp;

import dev.rono.extensions.shared.strategy.LinkedBlockRegistry;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class SocketLampOnBlockBreakListener implements OnBlockBreakListener {
    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        LinkedBlockRegistry.unregister(event.block().location());
        SocketLampSupport.LIGHT_LEVEL.remove(LinkedBlockRegistry.key(event.block().location()));
    }
}

