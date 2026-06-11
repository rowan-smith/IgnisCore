package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.strategy.IgnisStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyRegistry;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.model.BlockDefinition;

import java.util.Map;

import static dev.rono.igniscore.util.ConfigValueReader.getList;
import static dev.rono.igniscore.util.ConfigValueReader.getMap;
import static dev.rono.igniscore.util.ConfigValueReader.getString;

public class StrategyProfileResolver {
    private final IgnisStrategyRegistry strategyRegistry;

    @Inject
    public StrategyProfileResolver(IgnisStrategyRegistry strategyRegistry) {
        this.strategyRegistry = strategyRegistry;
    }

    public StrategyProfile resolve(BlockDefinition definition) {
        IgnisStrategy strategy = strategyRegistry.get(definition.getStrategy());
        StrategyProfile profile = strategy.profile(definition);
        Map<String, Object> interactions = definition.getInteractionSettings();

        StrategyProfile.Builder builder = profile.toBuilder()
                .placeable(definition.isPlaceable())
                .breakable(definition.isBreakable())
                .defaultFuse(definition.getFuse())
                .defaultRadius(definition.getRadius());

        if (definition.getDisplaySettings().containsKey("scale")) {
            Object scale = definition.getDisplaySettings().get("scale");
            if (scale instanceof Number number) {
                builder.displayScale(number.doubleValue());
            }
        }

        if (!interactions.isEmpty()) {
            builder.combustible(hasIgnitionConfigured(interactions));
        }

        return builder.build();
    }

    private boolean hasIgnitionConfigured(Map<String, Object> interactions) {
        if (interactions.containsKey("ignite")) {
            return true;
        }

        String rightClickAction = getString(getMap(interactions, "right_click"), "action",
                getString(getMap(interactions, "right_click"), "default_action", ""));
        if ("ignite".equalsIgnoreCase(rightClickAction)) {
            return true;
        }

        for (Object materialAction : getList(getMap(interactions, "left_click"), "material_actions")) {
            if (materialAction instanceof Map<?, ?> map) {
                Object action = map.get("action");
                if (action != null && "ignite".equalsIgnoreCase(action.toString())) {
                    return true;
                }
            }
        }

        return false;
    }
}
