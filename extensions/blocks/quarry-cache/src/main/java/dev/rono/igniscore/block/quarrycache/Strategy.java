package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final QuarryCacheRegistry registry;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.registry = new QuarryCacheRegistry(context);
    }

    @Override
    public void onPlaced(BlockDefinition definition, IgnisLocation location, IgnisItem placedFrom) {
        registry.register(location, definition, placedFrom);
    }

    @Override
    public void onPlacedInteract(BlockDefinition definition,
                                 IgnisLocation location,
                                 IgnisPlayer player,
                                 dev.rono.igniscore.api.port.IgnisInteraction interaction,
                                 IgnisItem heldItem,
                                 CustomBlockAction action) {
        if (action == CustomBlockAction.OPEN) {
            registry.openGui(player, location);
        }
    }

    @Override
    public void onPlacedBreak(BlockDefinition definition, IgnisLocation location, IgnisItem droppedItem) {
        registry.handleBreak(location, droppedItem);
    }
}
