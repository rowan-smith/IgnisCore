package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final QuarryCacheRegistry registry;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.registry = new QuarryCacheRegistry(context);
    }

        context.eventBus().subscribe(event -> registry.register(event.block(), event.definition(), event.placedFrom()));
        context.eventBus().subscribe(event -> registry.handleBreak(event.block(), event.droppedItem()));
        context.eventBus().subscribe(event -> {
            if (event.action() == CustomBlockAction.OPEN) {
                registry.openGui(event.player(), event.block());
            }
        });

}
