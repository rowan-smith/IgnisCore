package dev.rono.igniscore.block.saplingnursery;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        SaplingNurseryRuntime runtime = new SaplingNurseryRuntime(context);
        context.eventBus().subscribe(new SaplingNurseryOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new SaplingNurseryOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new SaplingNurseryOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
