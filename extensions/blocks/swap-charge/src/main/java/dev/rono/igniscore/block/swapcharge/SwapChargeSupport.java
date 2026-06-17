package dev.rono.igniscore.block.swapcharge;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

final class SwapChargeSupport {
    private SwapChargeSupport() {
    }

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

