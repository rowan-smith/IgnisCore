package dev.rono.igniscore.loader.support;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class TestBlockStrategy extends AbstractIgnisBlockStrategy {
    public TestBlockStrategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .combustible(false)
                .leftClickAction(dev.rono.igniscore.api.CustomBlockAction.BREAK)
                .rightClickAction(dev.rono.igniscore.api.CustomBlockAction.NONE)
                .build();
    }

    @Override
    public void registerEvents() {
    }
}
