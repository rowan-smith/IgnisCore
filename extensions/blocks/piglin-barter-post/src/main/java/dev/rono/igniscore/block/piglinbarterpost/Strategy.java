package dev.rono.igniscore.block.piglinbarterpost;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new PiglinBarterPostOnBlockClickListener());
        PiglinBarterPostRuntime runtime = new PiglinBarterPostRuntime(context);
        context.eventBus().subscribe(new PiglinBarterPostOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new PiglinBarterPostOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new PiglinBarterPostOnBlockInteractListener(runtime));
    }

}
