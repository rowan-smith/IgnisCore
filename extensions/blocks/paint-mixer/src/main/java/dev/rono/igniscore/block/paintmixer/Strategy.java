package dev.rono.igniscore.block.paintmixer;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new PaintMixerOnBlockClickListener());
        context.eventBus().subscribe(new PaintMixerOnBlockPlaceListener(context));
        context.eventBus().subscribe(new PaintMixerOnBlockBreakListener());
    }

}
