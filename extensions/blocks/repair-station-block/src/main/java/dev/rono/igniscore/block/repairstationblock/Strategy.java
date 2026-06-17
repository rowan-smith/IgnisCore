package dev.rono.igniscore.block.repairstationblock;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new RepairStationBlockOnBlockClickListener());
        context.eventBus().subscribe(new RepairStationBlockOnBlockInteractListener(context));
    }

}
