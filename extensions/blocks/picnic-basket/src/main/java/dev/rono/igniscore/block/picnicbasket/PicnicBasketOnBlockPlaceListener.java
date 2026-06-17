package dev.rono.igniscore.block.picnicbasket;

import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;

final class PicnicBasketOnBlockPlaceListener implements OnBlockPlaceListener {
    private final PicnicBasketRuntime runtime;

    PicnicBasketOnBlockPlaceListener(PicnicBasketRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), PicnicBasketSupport.title(runtime, event.block().definition()), 1);
    }
}

