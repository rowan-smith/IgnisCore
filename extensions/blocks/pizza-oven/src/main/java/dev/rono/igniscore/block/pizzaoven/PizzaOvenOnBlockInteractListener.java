package dev.rono.igniscore.block.pizzaoven;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.event.BlockInteractEvent;
import dev.rono.igniscore.api.event.OnBlockInteractListener;
import dev.rono.igniscore.block.pizzaoven.PizzaOvenSupport;

final class PizzaOvenOnBlockInteractListener implements OnBlockInteractListener {
    private final PizzaOvenRuntime runtime;

    PizzaOvenOnBlockInteractListener(PizzaOvenRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (event.action() == CustomBlockAction.OPEN) {
            runtime.registry.openBlock(event.player(), event.block().location());
        }
    }
}

