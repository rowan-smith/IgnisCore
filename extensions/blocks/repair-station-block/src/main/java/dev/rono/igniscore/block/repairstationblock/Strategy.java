package dev.rono.igniscore.block.repairstationblock;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final RepairStationBlockBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new RepairStationBlockBehavior(context);
        onBlockInteract(event -> behavior.onPlacedInteract(event.definition(), event.block(), event.player(), event.interaction(), event.heldItem(), event.action()));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

}
