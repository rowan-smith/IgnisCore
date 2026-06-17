package dev.rono.igniscore.block.scarecrowanchor;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new ScarecrowAnchorOnBlockClickListener());
        context.eventBus().subscribe(new ScarecrowAnchorOnBlockPlaceListener(context));
        context.eventBus().subscribe(new ScarecrowAnchorOnBlockBreakListener());
    }

}
