package dev.rono.igniscore.block.pizzaoven;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockPlaceEvent;
import dev.rono.igniscore.api.event.OnBlockPlaceListener;
import dev.rono.igniscore.api.strategy.StrategySupport;

final class PizzaOvenOnBlockPlaceListener implements OnBlockPlaceListener {
    private final PizzaOvenRuntime runtime;

    PizzaOvenOnBlockPlaceListener(PizzaOvenRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        runtime.registry.registerBlock(event.block().location(), PizzaOvenSupport.title(runtime, event.block().definition()), 3);
        PlacedTickSupport.start(runtime.context, event.block().location(), StrategySupport.customInt(event.block().definition(), "tickPeriod", 50),
                () -> PizzaOvenSupport.tick(runtime, event.block().definition(), event.block().location()));
    }
}

