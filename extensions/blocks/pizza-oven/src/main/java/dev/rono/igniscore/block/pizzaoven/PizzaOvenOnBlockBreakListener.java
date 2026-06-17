package dev.rono.igniscore.block.pizzaoven;

import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.igniscore.api.event.BlockBreakEvent;
import dev.rono.igniscore.api.event.OnBlockBreakListener;
import dev.rono.igniscore.block.pizzaoven.PizzaOvenSupport;

final class PizzaOvenOnBlockBreakListener implements OnBlockBreakListener {
    private final PizzaOvenRuntime runtime;

    PizzaOvenOnBlockBreakListener(PizzaOvenRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        PlacedTickSupport.stop(event.block().location());
        runtime.registry.unregister(event.block().location());
    }
}

