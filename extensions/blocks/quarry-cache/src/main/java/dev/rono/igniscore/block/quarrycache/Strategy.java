package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final QuarryCacheRegistry registry;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.registry = new QuarryCacheRegistry(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .combustible(false)
                .leftClickAction(CustomBlockAction.BREAK)
                .rightClickAction(CustomBlockAction.OPEN)
                .placementSound("BLOCK_CHEST_PLACE")
                .build();
    }

    @Override
    public void onStaticPlace(BlockDefinition definition, IgnisLocation location, IgnisItem placedFrom) {
        registry.register(location, definition, placedFrom);
    }

    @Override
    public void onStaticInteract(BlockDefinition definition, IgnisLocation location, IgnisPlayer player, CustomBlockAction action) {
        if (action == CustomBlockAction.OPEN) {
            registry.openGui(player, location);
        }
    }

    @Override
    public void onStaticBreak(BlockDefinition definition, IgnisLocation location, IgnisItem droppedItem) {
        registry.handleBreak(location, droppedItem);
    }
}
