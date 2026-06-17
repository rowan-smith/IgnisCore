package dev.rono.igniscore.block.antigravityzone;

import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisBlockStrategy {

    public Strategy(IgnisStrategyContext context) {
        super(context);
        AntiGravityZoneRuntime runtime = new AntiGravityZoneRuntime(context);
        context.eventBus().subscribe(new AntiGravityZoneOnBlockClickListener());
        context.eventBus().subscribe(new AntiGravityZoneOnBlockPlaceListener(runtime));
        context.eventBus().subscribe(new AntiGravityZoneOnBlockTriggerListener(runtime));
    }

}
