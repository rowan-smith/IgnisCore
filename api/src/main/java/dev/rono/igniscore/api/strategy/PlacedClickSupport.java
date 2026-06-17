package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;

import java.util.List;

/**
 * Default placed-block click resolution from block YAML behavior config.
 *
 * <p>Extensions implement {@link dev.rono.igniscore.api.event.OnBlockClickListener} for custom behavior;
 * use this helper when delegating to standard combustible or fixed click routing.</p>
 */
public final class PlacedClickSupport {
    private PlacedClickSupport() {
    }

    /**
     * Resolves the click action for a placed block using the held item's material key.
     *
     * @param definition block definition with behavior config
     * @param left left-click action when not overridden by combustible ignition rules
     * @param right right-click action when ignition does not apply
     * @param interaction player interaction type
     * @param heldItem item in the player's hand, or {@code null}
     * @return action instructing the core how to handle the click
     */
    public static CustomBlockAction resolve(BlockDefinition definition,
                                            CustomBlockAction left,
                                            CustomBlockAction right,
                                            IgnisInteraction interaction,
                                            IgnisItem heldItem) {
        return resolve(definition, left, right, interaction, materialKey(heldItem));
    }

    /**
     * Resolves the click action for a placed block using an explicit material key.
     *
     * <p>Left clicks return {@code left}. Right clicks return {@link CustomBlockAction#IGNITE} when
     * behavior marks the block combustible and the material is listed in {@code ignition_materials};
     * otherwise {@code right}. Other interaction types yield {@link CustomBlockAction#NONE}.</p>
     *
     * @param definition block definition with behavior config
     * @param left left-click action
     * @param right right-click action when ignition does not apply
     * @param interaction player interaction type
     * @param materialKey held item material key, or {@code AIR}
     * @return action instructing the core how to handle the click
     */
    public static CustomBlockAction resolve(BlockDefinition definition,
                                            CustomBlockAction left,
                                            CustomBlockAction right,
                                            IgnisInteraction interaction,
                                            String materialKey) {
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(definition.getBehaviorConfig());
        return switch (interaction) {
            case LEFT_CLICK_BLOCK -> left;
            case RIGHT_CLICK_BLOCK -> resolveRightClick(behavior, right, materialKey);
            default -> CustomBlockAction.NONE;
        };
    }

    /**
     * Returns whether the given material can ignite a combustible block per behavior config.
     *
     * @param definition block definition with behavior config
     * @param materialKey material key to test; {@code null}, blank, and {@code AIR} are never ignition materials
     * @return {@code true} when {@code materialKey} matches a configured ignition material
     */
    public static boolean isIgnitionMaterial(BlockDefinition definition, String materialKey) {
        return isIgnitionMaterial(BlockBehaviorConfig.from(definition.getBehaviorConfig()), materialKey);
    }

    /**
     * Returns whether the given material can ignite a combustible block per behavior config.
     *
     * @param behavior parsed behavior section
     * @param materialKey material key to test
     * @return {@code true} when {@code materialKey} matches a configured ignition material
     */
    public static boolean isIgnitionMaterial(BlockBehaviorConfig behavior, String materialKey) {
        if (materialKey == null || materialKey.isBlank() || "AIR".equalsIgnoreCase(materialKey)) {
            return false;
        }
        List<String> materials = behavior.ignitionMaterials();
        for (String material : materials) {
            if (materialKey.equalsIgnoreCase(material)) {
                return true;
            }
        }
        return false;
    }

    private static CustomBlockAction resolveRightClick(BlockBehaviorConfig behavior,
                                                       CustomBlockAction right,
                                                       String materialKey) {
        if (behavior.combustible() && isIgnitionMaterial(behavior, materialKey)) {
            return CustomBlockAction.IGNITE;
        }
        return right;
    }

    private static String materialKey(IgnisItem heldItem) {
        if (heldItem == null || heldItem.isAir()) {
            return "AIR";
        }
        String materialKey = heldItem.getMaterialKey();
        return materialKey == null || materialKey.isBlank() ? "AIR" : materialKey;
    }
}
