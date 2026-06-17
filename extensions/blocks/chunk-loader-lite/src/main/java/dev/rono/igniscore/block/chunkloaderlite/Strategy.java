package dev.rono.igniscore.block.chunkloaderlite;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new ChunkLoaderLiteOnBlockClickListener());
        ChunkLoaderLiteRuntime runtime = new ChunkLoaderLiteRuntime(context);
        context.eventBus().subscribe(new ChunkLoaderLiteOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new ChunkLoaderLiteOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new ChunkLoaderLiteOnBlockInteractListener(runtime));
    }

}
