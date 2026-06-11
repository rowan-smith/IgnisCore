package dev.rono.igniscore.strategies;

import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.BuiltinStrategyBootstrap;
import dev.rono.igniscore.model.BlockDefinition;
import dev.rono.igniscore.model.RuntimeBlockInstance;
import dev.rono.igniscore.service.CustomBlockAction;

public class StructureStrategy extends BaseBlockBehaviorStrategy {
    public StructureStrategy() {
        super(IgnisStrategyDescriptor.of("structure", "Structure Placement", "1.0.0", "IgnisCore"));
    }

    @Override
    public dev.rono.igniscore.api.strategy.StrategyProfile profile(BlockDefinition definition) {
        return BuiltinStrategyBootstrap.explosiveProfile().toBuilder()
                .combustible(false)
                .rightClickAction(CustomBlockAction.BREAK)
                .build();
    }
    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        // Placeholder for future structure placement logic
    }
}
