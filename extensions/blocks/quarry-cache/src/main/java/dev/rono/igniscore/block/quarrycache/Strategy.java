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

    @Override
    public void registerEvents() {
        onBlockPlace(event -> registry.register(event.location(), event.definition(), event.placedFrom()));
        onBlockBreak(event -> registry.handleBreak(event.location(), event.droppedItem()));
        onBlockInteract(event -> {
            if (event.action() == CustomBlockAction.OPEN) {
                registry.openGui(event.player(), event.location());
            }
        });
    }
}
