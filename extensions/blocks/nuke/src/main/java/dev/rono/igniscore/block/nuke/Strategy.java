package dev.rono.igniscore.block.nuke;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new NukeOnBlockClickListener());
        context.eventBus().subscribe(new NukeOnBlockPlaceListener(context));
        context.eventBus().subscribe(new NukeOnBlockActivateListener(context));
        context.eventBus().subscribe(new NukeOnBlockTickListener(context));
        context.eventBus().subscribe(new NukeOnBlockTriggerListener(context));
    }

}
