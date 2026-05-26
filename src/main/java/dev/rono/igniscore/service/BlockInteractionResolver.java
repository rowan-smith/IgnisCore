package dev.rono.igniscore.service;

import dev.rono.igniscore.model.BlockDefinition;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Locale;

import static dev.rono.igniscore.util.ConfigValueReader.getList;
import static dev.rono.igniscore.util.ConfigValueReader.getMap;
import static dev.rono.igniscore.util.ConfigValueReader.getString;

public class BlockInteractionResolver {
    private static final String ACTION_BREAK = "break";
    private static final String ACTION_IGNITE = "ignite";

    public CustomBlockAction resolve(BlockDefinition definition, Action clickAction, ItemStack item) {
        String action = getConfiguredAction(definition, clickAction, item);
        return switch (action) {
            case ACTION_BREAK -> CustomBlockAction.BREAK;
            case ACTION_IGNITE -> CustomBlockAction.IGNITE;
            default -> CustomBlockAction.NONE;
        };
    }

    private String getConfiguredAction(BlockDefinition definition, Action clickAction, ItemStack item) {
        String clickKey = clickAction == Action.LEFT_CLICK_BLOCK ? "left_click" : "right_click";
        Map<String, Object> clickSettings = getMap(definition.getInteractionSettings(), clickKey);
        for (Object materialAction : getList(clickSettings, "material_actions")) {
            if (!(materialAction instanceof Map<?, ?> rawMap)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            String action = getString(map, "action", "");
            if (!action.isEmpty() && matchesConfiguredMaterials(map, item)) {
                return action.toLowerCase(Locale.ROOT);
            }
        }

        String configuredAction = getString(clickSettings, "action", getString(clickSettings, "default_action", ""));
        if (!configuredAction.isEmpty()) {
            if (ACTION_IGNITE.equalsIgnoreCase(configuredAction) && !matchesConfiguredMaterials(clickSettings, item)) {
                return "";
            }
            return configuredAction.toLowerCase(Locale.ROOT);
        }

        if (matchesDefaultIgnitionMaterial(item)) {
            return ACTION_IGNITE;
        }

        return clickAction == Action.LEFT_CLICK_BLOCK ? ACTION_BREAK : "";
    }

    private boolean matchesConfiguredMaterials(Map<String, Object> settings, ItemStack item) {
        List<?> materials = getList(settings, "materials");
        if (materials.isEmpty()) {
            return true;
        }
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        String itemType = item.getType().name();
        for (Object material : materials) {
            if (material != null && itemType.equalsIgnoreCase(material.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesDefaultIgnitionMaterial(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        Material type = item.getType();
        return type == Material.FLINT_AND_STEEL || type == Material.FIRE_CHARGE || type == Material.FLINT;
    }
}
