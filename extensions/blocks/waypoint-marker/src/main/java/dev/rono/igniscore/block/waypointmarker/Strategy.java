package dev.rono.igniscore.block.waypointmarker;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new WaypointMarkerOnBlockClickListener());
        context.eventBus().subscribe(new WaypointMarkerOnBlockInteractListener(context));
    }

}
