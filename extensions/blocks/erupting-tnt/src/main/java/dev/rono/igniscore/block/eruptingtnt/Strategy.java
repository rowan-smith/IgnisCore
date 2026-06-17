package dev.rono.igniscore.block.eruptingtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {
    private final EruptingBehavior behavior;

    public Strategy(IgnisStrategyContext context) {
        super(context);
        this.behavior = new EruptingBehavior(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(100)
                .build();
    }

    @Override
    public void registerEvents() {
        onBlockTick(event -> behavior.onTick(event.instance(), event.instance().getDefinition()));
        onBlockTrigger(event -> behavior.onTrigger(event.instance()));
    }
}
