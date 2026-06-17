package dev.rono.igniscore.block.cropmri;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new CropMriOnBlockClickListener());
        context.eventBus().subscribe(new CropMriOnBlockPlaceListener(context));
        context.eventBus().subscribe(new CropMriOnBlockBreakListener());
    }

}
