package dev.rono.examples.sonicboom;

import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.IgnisStrategyPlugin;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;

public class SonicBoomStrategyPlugin implements IgnisStrategyPlugin {
    public static final String PLUGIN_ID = "sonic-boom-strategies";

    @Override
    public void onLoad(IgnisStrategyRegistry registry, IgnisStrategyContext context) {
        registry.register(
                IgnisStrategyDescriptor.of("sonic_boom", "Sonic Boom", "1.0.0", "IgnisCore Examples", PLUGIN_ID),
                new SonicBoomStrategy(context)
        );
    }
}
