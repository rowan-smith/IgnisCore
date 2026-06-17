package dev.rono.igniscore.block.chunkloaderlite;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.block.chunkloaderlite.ChunkLoaderLiteSupport;

final class ChunkLoaderLiteOnBlockInteractListener implements OnBlockInteractListener {
    private final ChunkLoaderLiteRuntime runtime;

    ChunkLoaderLiteOnBlockInteractListener(ChunkLoaderLiteRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() == CustomBlockAction.OPEN) {
            runtime.registry.openBlock(event.player(), event.block().location());
        }
    }
}

