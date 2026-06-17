package dev.rono.igniscore.block.chickencoopcache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.block.chickencoopcache.ChickenCoopCacheSupport;

final class ChickenCoopCacheOnBlockInteractListener implements OnBlockInteractListener {
    private final ChickenCoopCacheRuntime runtime;

    ChickenCoopCacheOnBlockInteractListener(ChickenCoopCacheRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() == CustomBlockAction.OPEN) {
            runtime.registry.openBlock(event.player(), event.block().location());
        }
    }
}

