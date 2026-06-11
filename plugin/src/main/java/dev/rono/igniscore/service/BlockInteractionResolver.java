package dev.rono.igniscore.service;

import dev.rono.igniscore.api.CustomBlockAction;

import com.google.inject.Inject;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.api.model.BlockDefinition;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static dev.rono.igniscore.util.ConfigValueReader.getList;
import static dev.rono.igniscore.util.ConfigValueReader.getMap;
import static dev.rono.igniscore.util.ConfigValueReader.getString;

public class BlockInteractionResolver {
    private static final String ACTION_BREAK = "break";
    private static final String ACTION_IGNITE = "ignite";

    private final StrategyProfileResolver profileResolver;

    @Inject
    public BlockInteractionResolver(StrategyProfileResolver profileResolver) {
        this.profileResolver = profileResolver;
    }

    public CustomBlockAction resolve(BlockDefinition definition, Action clickAction, ItemStack item) {
        return resolve(definition, clickAction, item != null ? item.getType() : null);
    }

    public CustomBlockAction resolve(BlockDefinition definition, Action clickAction, Material itemType) {
        String action = getConfiguredAction(definition, clickAction, itemType);
        return switch (action) {
            case ACTION_BREAK -> CustomBlockAction.BREAK;
            case ACTION_IGNITE -> CustomBlockAction.IGNITE;
            default -> CustomBlockAction.NONE;
        };
    }

    private String getConfiguredAction(BlockDefinition definition, Action clickAction, Material itemType) {
        String clickKey = clickAction == Action.LEFT_CLICK_BLOCK ? "left_click" : "right_click";
        Map<String, Object> clickSettings = getMap(definition.getInteractionSettings(), clickKey);
        for (Object materialAction : getList(clickSettings, "material_actions")) {
            if (!(materialAction instanceof Map<?, ?> rawMap)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            String action = getString(map, "action", "");
            if (!action.isEmpty() && matchesConfiguredMaterials(map, itemType)) {
                return action.toLowerCase(Locale.ROOT);
            }
        }

        String configuredAction = getString(clickSettings, "action", getString(clickSettings, "default_action", ""));
        if (!configuredAction.isEmpty()) {
            if (ACTION_IGNITE.equalsIgnoreCase(configuredAction) && !matchesConfiguredMaterials(clickSettings, itemType)) {
                return "";
            }
            return configuredAction.toLowerCase(Locale.ROOT);
        }

        StrategyProfile profile = profileResolver.resolve(definition);
        if (!profile.isCombustible()) {
            return clickAction == Action.LEFT_CLICK_BLOCK
                    ? toActionName(profile.getLeftClickAction())
                    : toActionName(profile.getRightClickAction());
        }

        if (matchesIgnitionMaterial(profile.getIgnitionMaterials(), itemType) || matchesDefaultIgnitionMaterial(itemType)) {
            return ACTION_IGNITE;
        }

        CustomBlockAction fallback = clickAction == Action.LEFT_CLICK_BLOCK
                ? profile.getLeftClickAction()
                : profile.getRightClickAction();
        return toActionName(fallback);
    }

    private String toActionName(CustomBlockAction action) {
        return switch (action) {
            case BREAK -> ACTION_BREAK;
            case IGNITE -> ACTION_IGNITE;
            case NONE -> "";
        };
    }

    private boolean matchesConfiguredMaterials(Map<String, Object> settings, Material itemType) {
        List<?> materials = getList(settings, "materials");
        if (materials.isEmpty()) {
            return true;
        }
        if (itemType == null || itemType == Material.AIR) {
            return false;
        }

        String name = itemType.name();
        for (Object material : materials) {
            if (material != null && name.equalsIgnoreCase(material.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesIgnitionMaterial(List<String> materials, Material itemType) {
        if (itemType == null || itemType == Material.AIR) {
            return false;
        }

        String name = itemType.name();
        for (String material : materials) {
            if (name.equalsIgnoreCase(material)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesDefaultIgnitionMaterial(Material itemType) {
        if (itemType == null || itemType == Material.AIR) {
            return false;
        }
        return itemType == Material.FLINT_AND_STEEL || itemType == Material.FIRE_CHARGE || itemType == Material.FLINT;
    }
}
