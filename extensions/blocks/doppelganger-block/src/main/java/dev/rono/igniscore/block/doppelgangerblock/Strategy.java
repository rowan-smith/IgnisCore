package dev.rono.igniscore.block.doppelgangerblock;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        DoppelgangerBlockRuntime runtime = new DoppelgangerBlockRuntime(context);
        context.eventBus().subscribe(new DoppelgangerBlockOnBlockClickListener());
        context.eventBus().subscribe(new DoppelgangerBlockOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new DoppelgangerBlockOnBlockTriggerListener(runtime));
    }

}
