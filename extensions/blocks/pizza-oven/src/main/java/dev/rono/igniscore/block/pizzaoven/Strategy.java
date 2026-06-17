package dev.rono.igniscore.block.pizzaoven;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new PizzaOvenOnBlockClickListener());
        PizzaOvenRuntime runtime = new PizzaOvenRuntime(context);
        context.eventBus().subscribe(new PizzaOvenOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new PizzaOvenOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new PizzaOvenOnBlockInteractListener(runtime));
    }

}
