package dev.rono.igniscore.block.picnicbasket;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        PicnicBasketRuntime runtime = new PicnicBasketRuntime(context);
        context.eventBus().subscribe(new PicnicBasketOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new PicnicBasketOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new PicnicBasketOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
