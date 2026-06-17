package dev.rono.igniscore.block.pocketdimensioncache;

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
        PocketDimensionCacheRuntime runtime = new PocketDimensionCacheRuntime(context);
        context.eventBus().subscribe(new PocketDimensionCacheOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new PocketDimensionCacheOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new PocketDimensionCacheOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
