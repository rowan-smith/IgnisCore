package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.model.BlockDefinition;

public class StrategyProfileResolver {
    private final IgnisStrategyRegistry strategyRegistry;

    @Inject
    public StrategyProfileResolver(IgnisStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
    }

    public StrategyProfile resolve(BlockDefinition definition) {
        IgnisBlockStrategy blockStrategy = strategyRegistry.requireBlockStrategy(
                definition.getExtensionId(), definition.getId());
        StrategyProfile profile = blockStrategy.profile(definition);
        profile = BlockBehaviorConfig.from(definition.getBehaviorConfig()).merge(profile);

        StrategyProfile.Builder builder = profile.toBuilder()
                .placeable(definition.isPlaceable())
                .breakable(definition.isBreakable())
                .defaultFuse(StrategySupport.customInt(definition.getCustomData(), "fuse", profile.getDefaultFuse()))
                .defaultRadius(StrategySupport.customDouble(definition.getCustomData(), "radius", profile.getDefaultRadius()));

        if (definition.getDisplaySettings().containsKey("scale")) {
            Object scale = definition.getDisplaySettings().get("scale");
            if (scale instanceof Number number) {
                builder.displayScale(number.doubleValue());
            }
        }

        return builder.build();
    }
}
