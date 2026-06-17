package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;

import java.util.List;

/**
 * Default placed-block click resolution from a {@link StrategyProfile}.
 *
 * <p>Extensions implement {@link dev.rono.igniscore.api.event.OnBlockClickListener} for custom behavior;
 * use this helper when delegating to profile defaults or composing profile rules with extension logic.</p>
 */
public final class PlacedClickSupport {
    private PlacedClickSupport() {
    }

    /**
     * Resolves the click action for a placed block using the held item's material key.
     *
     * @param profile strategy behavior profile
     * @param interaction player interaction type
     * @param heldItem item in the player's hand, or {@code null}
     * @return action instructing the core how to handle the click
     */
    public static CustomBlockAction resolve(StrategyProfile profile, IgnisInteraction interaction, IgnisItem heldItem) {
        return resolve(profile, interaction, materialKey(heldItem));
    }

    /**
     * Resolves the click action for a placed block using an explicit material key.
     *
     * <p>Left clicks return {@link StrategyProfile#getLeftClickAction()}. Right clicks return
     * {@link CustomBlockAction#IGNITE} when the profile is combustible and the material is listed
     * in {@link StrategyProfile#getIgnitionMaterials()}; otherwise
     * {@link StrategyProfile#getRightClickAction()}. Other interaction types yield
     * {@link CustomBlockAction#NONE}.</p>
     *
     * @param profile strategy behavior profile
     * @param interaction player interaction type
     * @param materialKey held item material key, or {@code AIR}
     * @return action instructing the core how to handle the click
     */
    public static CustomBlockAction resolve(StrategyProfile profile, IgnisInteraction interaction, String materialKey) {
        return switch (interaction) {
            case LEFT_CLICK_BLOCK -> profile.getLeftClickAction();
            case RIGHT_CLICK_BLOCK -> resolveRightClick(profile, materialKey);
            default -> CustomBlockAction.NONE;
        };
    }

    /**
     * Returns whether the given material can ignite a combustible block per the profile.
     *
     * @param profile strategy behavior profile
     * @param materialKey material key to test; {@code null}, blank, and {@code AIR} are never ignition materials
     * @return {@code true} when {@code materialKey} matches an entry in {@link StrategyProfile#getIgnitionMaterials()}
     */
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
        return false;
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
