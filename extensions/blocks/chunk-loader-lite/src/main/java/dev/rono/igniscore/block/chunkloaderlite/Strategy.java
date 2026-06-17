package dev.rono.igniscore.block.chunkloaderlite;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.OPEN));
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
