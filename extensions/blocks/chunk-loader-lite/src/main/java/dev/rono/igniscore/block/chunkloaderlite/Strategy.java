package dev.rono.igniscore.block.chunkloaderlite;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        ChunkLoaderLiteRuntime runtime = new ChunkLoaderLiteRuntime(context);
        context.eventBus().subscribe(new ChunkLoaderLiteOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new ChunkLoaderLiteOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new ChunkLoaderLiteOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
