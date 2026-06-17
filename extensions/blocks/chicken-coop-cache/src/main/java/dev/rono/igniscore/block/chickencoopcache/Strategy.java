package dev.rono.igniscore.block.chickencoopcache;

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
        ChickenCoopCacheRuntime runtime = new ChickenCoopCacheRuntime(context);
        context.eventBus().subscribe(new ChickenCoopCacheOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new ChickenCoopCacheOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new ChickenCoopCacheOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
