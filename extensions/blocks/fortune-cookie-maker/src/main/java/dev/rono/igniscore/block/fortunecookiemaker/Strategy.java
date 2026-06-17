package dev.rono.igniscore.block.fortunecookiemaker;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.CustomBlockAction;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        FortuneCookieMakerRuntime runtime = new FortuneCookieMakerRuntime(context);
        context.eventBus().subscribe(new FortuneCookieMakerOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new FortuneCookieMakerOnBlockBreakListener(runtime));
        context.eventBus().subscribe(new FortuneCookieMakerOnBlockInteractListener(runtime));
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }

}
