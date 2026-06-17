package dev.rono.igniscore.block.keyedhatch;

import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class KeyedHatchSupport {
    private KeyedHatchSupport() {
    }

    static final Map<String, Boolean> OPEN = new ConcurrentHashMap<>();

    static IgnisWorld worldAt(IgnisStrategyContext ctx, IgnisLocation location) {

        return ctx.extensions().resolveWorld(location);
    
    }
}

