package dev.rono.igniscore.block.barnbell;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class BarnBellSupport {
    private BarnBellSupport() {
    }

    static final Map<String, Long> COOLDOWN = new ConcurrentHashMap<>();

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

