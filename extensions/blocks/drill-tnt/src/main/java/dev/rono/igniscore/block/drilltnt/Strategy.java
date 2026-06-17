package dev.rono.igniscore.block.drilltnt;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        DrillRuntime runtime = new DrillRuntime(context);
        context.eventBus().subscribe(new DrillOnBlockClickListener());
        context.eventBus().subscribe(new DrillOnBlockTickListener(runtime));
        context.eventBus().subscribe(new DrillOnBlockTriggerListener(runtime));
    }

}
