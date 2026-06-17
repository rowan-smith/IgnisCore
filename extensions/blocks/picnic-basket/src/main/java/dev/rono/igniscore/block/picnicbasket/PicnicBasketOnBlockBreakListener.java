package dev.rono.igniscore.block.picnicbasket;

import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;

final class PicnicBasketOnBlockBreakListener implements OnBlockBreakListener {
    private final PicnicBasketRuntime runtime;

    PicnicBasketOnBlockBreakListener(PicnicBasketRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        runtime.registry.unregister(event.block().location());
        PicnicBasketSupport.LAST_OPEN.remove(PicnicBasketSupport.blockKey(event.block().location()));
    }
}

