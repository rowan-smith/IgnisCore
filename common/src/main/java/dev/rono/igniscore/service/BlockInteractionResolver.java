package dev.rono.igniscore.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.StrategyProfile;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static dev.rono.igniscore.util.ConfigValueReader.getList;
import static dev.rono.igniscore.util.ConfigValueReader.getMap;
import static dev.rono.igniscore.util.ConfigValueReader.getString;

@Singleton
public class BlockInteractionResolver {
    private static final String ACTION_BREAK = "break";
    private static final String ACTION_IGNITE = "ignite";
    private static final String ACTION_OPEN = "open";
    private static final String LEFT_CLICK = "LEFT_CLICK";
    private static final String RIGHT_CLICK = "RIGHT_CLICK";

    private final StrategyProfileResolver profileResolver;

    @Inject
    public BlockInteractionResolver(StrategyProfileResolver profileResolver) {
        this.profileResolver = profileResolver;
    }

    public CustomBlockAction resolve(BlockDefinition definition, String clickSide, String materialKey) {
        String action = getConfiguredAction(definition, clickSide, materialKey);
        return switch (action) {
            case ACTION_BREAK -> CustomBlockAction.BREAK;
            case ACTION_IGNITE -> CustomBlockAction.IGNITE;
            case ACTION_OPEN -> CustomBlockAction.OPEN;
            default -> CustomBlockAction.NONE;
        };
    }

    private String getConfiguredAction(BlockDefinition definition, String clickSide, String materialKey) {
        String clickKey = LEFT_CLICK.equalsIgnoreCase(clickSide) ? "left_click" : "right_click";
        Map<String, Object> clickSettings = getMap(definition.getInteractionSettings(), clickKey);
        for (Object materialAction : getList(clickSettings, "material_actions")) {
            if (!(materialAction instanceof Map<?, ?> rawMap)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            String action = getString(map, "action", "");
            if (!action.isEmpty() && matchesConfiguredMaterials(map, materialKey)) {
                return action.toLowerCase(Locale.ROOT);
            }
        }

        String configuredAction = getString(clickSettings, "action", getString(clickSettings, "default_action", ""));
        if (!configuredAction.isEmpty()) {
            if (ACTION_IGNITE.equalsIgnoreCase(configuredAction) && !matchesConfiguredMaterials(clickSettings, materialKey)) {
                return "";
            }
            return configuredAction.toLowerCase(Locale.ROOT);
        }

        StrategyProfile profile = profileResolver.resolve(definition);
        if (!profile.isCombustible()) {
            return LEFT_CLICK.equalsIgnoreCase(clickSide)
                    ? toActionName(profile.getLeftClickAction())
                    : toActionName(profile.getRightClickAction());
        }

        if (matchesIgnitionMaterial(profile.getIgnitionMaterials(), materialKey) || matchesDefaultIgnitionMaterial(materialKey)) {
            return ACTION_IGNITE;
        }

        CustomBlockAction fallback = LEFT_CLICK.equalsIgnoreCase(clickSide)
                ? profile.getLeftClickAction()
                : profile.getRightClickAction();
        return toActionName(fallback);
    }

    private String toActionName(CustomBlockAction action) {
        return switch (action) {
            case BREAK -> ACTION_BREAK;
            case IGNITE -> ACTION_IGNITE;
            case OPEN -> ACTION_OPEN;
            case NONE -> "";
        };
    }

    private boolean matchesConfiguredMaterials(Map<String, Object> settings, String materialKey) {
        List<?> materials = getList(settings, "materials");
        if (materials.isEmpty()) {
            return true;
        }
        if (materialKey == null || materialKey.isBlank() || "AIR".equalsIgnoreCase(materialKey)) {
            return false;
        }

        for (Object material : materials) {
            if (material != null && materialKey.equalsIgnoreCase(material.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesIgnitionMaterial(List<String> materials, String materialKey) {
        if (materialKey == null || materialKey.isBlank() || "AIR".equalsIgnoreCase(materialKey)) {
            return false;
        }

        for (String material : materials) {
            if (materialKey.equalsIgnoreCase(material)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesDefaultIgnitionMaterial(String materialKey) {
        if (materialKey == null || materialKey.isBlank() || "AIR".equalsIgnoreCase(materialKey)) {
            return false;
        }
        return "FLINT_AND_STEEL".equalsIgnoreCase(materialKey)
                || "FIRE_CHARGE".equalsIgnoreCase(materialKey)
                || "FLINT".equalsIgnoreCase(materialKey);
    }
}
