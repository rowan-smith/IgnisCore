package dev.rono.igniscore.block.spiderstormtnt;

import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.combustible());
        SpiderStormRuntime runtime = new SpiderStormRuntime(context);
        context.eventBus().subscribe(new SpiderStormOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new SpiderStormOnBlockTriggerListener(runtime));
    }

}
