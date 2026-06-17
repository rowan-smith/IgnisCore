package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.model.BlockDefinition;

import java.util.Map;

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
                .breakable(definition.isBreakable());

        Map<String, Object> customData = definition.getCustomData();
        if (profile.hasFuseLifecycle() || customData.containsKey("fuse")) {
            builder.defaultFuse(StrategySupport.customInt(customData, "fuse", profile.getDefaultFuse()));
        }
        if (profile.hasExplosionRadius() || customData.containsKey("radius")) {
            builder.defaultRadius(StrategySupport.customDouble(customData, "radius", profile.getDefaultRadius()));
        }

        if (definition.getDisplaySettings().containsKey("scale")) {
            Object scale = definition.getDisplaySettings().get("scale");
            if (scale instanceof Number number) {
                builder.displayScale(number.doubleValue());
            }
        }

        return builder.build();
    }
}
