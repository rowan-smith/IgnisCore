package dev.rono.igniscore.block.coffeebrewer;

import dev.rono.extensions.shared.strategy.PlacedClickListener;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(PlacedClickListener.fixed(CustomBlockAction.BREAK, CustomBlockAction.OPEN));
        CoffeeBrewerRuntime runtime = new CoffeeBrewerRuntime(context);
        context.eventBus().subscribe(new CoffeeBrewerOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new CoffeeBrewerOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new CoffeeBrewerOnBlockInteractListener(runtime));
    }

}
