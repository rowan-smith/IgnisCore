package dev.rono.igniscore.block.checkpointobelisk;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new CheckpointObeliskOnBlockClickListener());
        context.eventBus().subscribe(new CheckpointObeliskOnBlockInteractListener(context));
    }

}
