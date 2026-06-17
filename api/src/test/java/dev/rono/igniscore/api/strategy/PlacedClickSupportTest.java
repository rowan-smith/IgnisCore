package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.config.BlockBehaviorConfig;
import dev.rono.igniscore.api.config.ExtensionConfig;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisInteraction;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacedClickSupportTest {

    @Test
    void neutralDefaultsDoNotAssignClickActions() {
        BlockDefinition definition = blockDefinition(Map.of());

        assertEquals(CustomBlockAction.NONE,
                PlacedClickSupport.resolve(definition, CustomBlockAction.NONE, CustomBlockAction.NONE,
                        IgnisInteraction.LEFT_CLICK_BLOCK, "AIR"));
        assertEquals(CustomBlockAction.NONE,
                PlacedClickSupport.resolve(definition, CustomBlockAction.NONE, CustomBlockAction.NONE,
                        IgnisInteraction.RIGHT_CLICK_BLOCK, "FLINT_AND_STEEL"));
    }

    @Test
    void leftClickUsesConfiguredAction() {
        BlockDefinition definition = blockDefinition(Map.of());

        assertEquals(CustomBlockAction.BREAK,
                PlacedClickSupport.resolve(definition, CustomBlockAction.BREAK, CustomBlockAction.NONE,
                        IgnisInteraction.LEFT_CLICK_BLOCK, "AIR"));
    }

    @Test
    void rightClickWithIgnitionItemIgnitesCombustibleBlocks() {
        BlockDefinition definition = blockDefinition(Map.of(
                "combustible", true,
                "ignition_materials", List.of("FLINT_AND_STEEL", "FIRE_CHARGE", "FLINT")));

        assertEquals(CustomBlockAction.IGNITE,
                PlacedClickSupport.resolve(definition, CustomBlockAction.BREAK, CustomBlockAction.NONE,
                        IgnisInteraction.RIGHT_CLICK_BLOCK, "FLINT_AND_STEEL"));
    }

    @Test
    void nonCombustibleBlocksUseConfiguredRightClickAction() {
        BlockDefinition definition = blockDefinition(Map.of());

        assertEquals(CustomBlockAction.OPEN,
                PlacedClickSupport.resolve(definition, CustomBlockAction.BREAK, CustomBlockAction.OPEN,
                        IgnisInteraction.RIGHT_CLICK_BLOCK, "FLINT_AND_STEEL"));
    }

    @Test
    void customIgnitionMaterialsAreHonored() {
        BlockDefinition definition = blockDefinition(Map.of(
                "combustible", true,
                "ignition_materials", List.of("STICK")));

        assertEquals(CustomBlockAction.IGNITE,
                PlacedClickSupport.resolve(definition, CustomBlockAction.BREAK, CustomBlockAction.NONE,
                        IgnisInteraction.RIGHT_CLICK_BLOCK, "STICK"));
        assertEquals(CustomBlockAction.NONE,
                PlacedClickSupport.resolve(definition, CustomBlockAction.BREAK, CustomBlockAction.NONE,
                        IgnisInteraction.RIGHT_CLICK_BLOCK, "AIR"));
    }

    @Test
    void airClicksIgnoreNonBlockInteractions() {
        BlockDefinition definition = blockDefinition(Map.of());

        assertEquals(CustomBlockAction.NONE,
                PlacedClickSupport.resolve(definition, CustomBlockAction.NONE, CustomBlockAction.NONE,
                        IgnisInteraction.RIGHT_CLICK_AIR, "FLINT_AND_STEEL"));
    }

    @Test
    void detectsIgnitionMaterialsFromBehaviorConfig() {
        BlockBehaviorConfig behavior = BlockBehaviorConfig.from(ExtensionConfig.of(Map.of(
                "ignition_materials", List.of("STICK"))));

        assertTrue(PlacedClickSupport.isIgnitionMaterial(behavior, "STICK"));
        assertFalse(PlacedClickSupport.isIgnitionMaterial(behavior, "FLINT_AND_STEEL"));
        assertFalse(PlacedClickSupport.isIgnitionMaterial(behavior, "STONE"));
    }

    private BlockDefinition blockDefinition(Map<String, Object> behavior) {
        return new BlockDefinition(
                "test",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of(),
                Map.of(),
                behavior,
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false,
                "test");
    }
}
