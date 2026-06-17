package dev.rono.igniscore.block.spiderstormtnt;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        SpiderStormRuntime runtime = new SpiderStormRuntime(context);
        context.eventBus().subscribe(new SpiderStormOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new SpiderStormOnBlockTriggerListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.builder()
                .defaultFuse(80)
                .build();
    }

}
