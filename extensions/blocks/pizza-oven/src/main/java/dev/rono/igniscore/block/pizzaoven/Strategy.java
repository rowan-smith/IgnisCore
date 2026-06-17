package dev.rono.igniscore.block.pizzaoven;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        PizzaOvenRuntime runtime = new PizzaOvenRuntime(context);
        context.eventBus().subscribe(new PizzaOvenOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new PizzaOvenOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new PizzaOvenOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
