package dev.rono.igniscore.block.fakebedrock;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        FakeBedrockRuntime runtime = new FakeBedrockRuntime(context);
        context.eventBus().subscribe(new FakeBedrockOnBlockClickListener());
        context.eventBus().subscribe(new FakeBedrockOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new FakeBedrockOnBlockTriggerListener(runtime));
    }

}
