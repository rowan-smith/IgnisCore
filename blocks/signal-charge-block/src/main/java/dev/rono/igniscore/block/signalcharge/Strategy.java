package dev.rono.igniscore.block.signalcharge;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final SignalChargeBehavior behavior = new SignalChargeBehavior();

    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .combustible(false)
                .leftClickAction(CustomBlockAction.NONE)
                .rightClickAction(CustomBlockAction.NONE)
                .placementSound("BLOCK_TNT_PLACE")
                .build();
    }

    @Override
    public void onPlace(RuntimeBlockInstance instance) {
        instance.setTicksLeft(0);
    }

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object context) {
        behavior.onTrigger(instance);
    }
}
