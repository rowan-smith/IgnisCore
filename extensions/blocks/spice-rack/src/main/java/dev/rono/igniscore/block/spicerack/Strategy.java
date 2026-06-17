package dev.rono.igniscore.block.spicerack;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final SpiceRackBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new SpiceRackBehavior(context);
        onBlockPlace(event -> behavior.onPlaced(event.definition(), event.block()));
        onBlockBreak(event -> behavior.onPlacedBreak(event.definition(), event.block()));
        onBlockInteract(event -> behavior.onPlacedInteract(event.definition(), event.block(), event.player(), event.interaction(), event.heldItem(), event.action()));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

}
