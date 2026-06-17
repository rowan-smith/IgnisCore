package dev.rono.igniscore.block.cropaccelerator;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new CropAcceleratorOnBlockClickListener());
        context.eventBus().subscribe(new CropAcceleratorOnBlockPlaceListener(context));
        context.eventBus().subscribe(new CropAcceleratorOnBlockBreakListener());
    }

}
