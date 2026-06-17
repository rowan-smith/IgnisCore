package dev.rono.igniscore.block.kelpcompressor;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new KelpCompressorOnBlockClickListener());
        context.eventBus().subscribe(new KelpCompressorOnBlockPlaceListener(context));
        context.eventBus().subscribe(new KelpCompressorOnBlockBreakListener());
    }

}
