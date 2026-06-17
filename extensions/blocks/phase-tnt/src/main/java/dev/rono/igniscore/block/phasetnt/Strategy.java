package dev.rono.igniscore.block.phasetnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final PhaseTntBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new PhaseTntBehavior(context);
        onBlockTick(event -> behavior.onTick(event.instance()));
        onBlockTrigger(event -> behavior.onTrigger(event.instance(), event.triggerContext()));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(60)
                .build();
    }

}
