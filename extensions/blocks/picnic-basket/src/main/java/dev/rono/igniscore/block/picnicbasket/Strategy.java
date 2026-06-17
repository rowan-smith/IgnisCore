package dev.rono.igniscore.block.picnicbasket;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new PicnicBasketOnBlockClickListener());
        PicnicBasketRuntime runtime = new PicnicBasketRuntime(context);
        context.eventBus().subscribe(new PicnicBasketOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new PicnicBasketOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new PicnicBasketOnBlockInteractListener(runtime));
    }

}
