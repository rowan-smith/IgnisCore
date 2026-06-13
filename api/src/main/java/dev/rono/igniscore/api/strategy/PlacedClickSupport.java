package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;

import java.util.List;

/**
 * Default placed-block click resolution from a {@link StrategyProfile}.
 * Extensions override {@link IgnisBlockStrategy#onPlacedClick} for custom behavior; use this helper
 * when delegating to profile defaults or composing profile rules with extension logic.
 */
public final class PlacedClickSupport {
    private PlacedClickSupport() {
    }

    public static CustomBlockAction resolve(StrategyProfile profile, IgnisInteraction interaction, IgnisItem heldItem) {
        return resolve(profile, interaction, materialKey(heldItem));
    }

    public static CustomBlockAction resolve(StrategyProfile profile, IgnisInteraction interaction, String materialKey) {
        return switch (interaction) {
            case LEFT_CLICK_BLOCK -> profile.getLeftClickAction();
            case RIGHT_CLICK_BLOCK -> resolveRightClick(profile, materialKey);
            default -> CustomBlockAction.NONE;
        };
    }

    public static boolean isIgnitionMaterial(StrategyProfile profile, String materialKey) {
        if (materialKey == null || materialKey.isBlank() || "AIR".equalsIgnoreCase(materialKey)) {
            return false;
        }
        List<String> materials = profile.getIgnitionMaterials();
        for (String material : materials) {
            if (materialKey.equalsIgnoreCase(material)) {
                return true;
            }
        }
        return "FLINT_AND_STEEL".equalsIgnoreCase(materialKey)
                || "FIRE_CHARGE".equalsIgnoreCase(materialKey)
                || "FLINT".equalsIgnoreCase(materialKey);
    }

    private static CustomBlockAction resolveRightClick(StrategyProfile profile, String materialKey) {
        if (profile.isCombustible() && isIgnitionMaterial(profile, materialKey)) {
            return CustomBlockAction.IGNITE;
        }
        return profile.getRightClickAction();
    }

    private static String materialKey(IgnisItem heldItem) {
        if (heldItem == null || heldItem.isAir()) {
            return "AIR";
        }
        String materialKey = heldItem.getMaterialKey();
        return materialKey == null || materialKey.isBlank() ? "AIR" : materialKey;
    }
}
