package dev.rono.igniscore.block.waypointmarker;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final WaypointMarkerBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new WaypointMarkerBehavior(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }

    @Override
    public void registerEvents() {
        onBlockInteract(event -> behavior.onPlacedInteract(event.definition(), event.location(), event.player(), event.interaction(), event.heldItem(), event.action()));
    }
}
