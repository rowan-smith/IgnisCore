package dev.rono.igniscore.api.strategy;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.port.IgnisInteraction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlacedClickSupportTest {

    @Test
    void neutralDefaultsDoNotAssignClickActions() {
        StrategyProfile profile = StrategyProfile.defaults();

        assertEquals(CustomBlockAction.NONE,
                PlacedClickSupport.resolve(profile, IgnisInteraction.LEFT_CLICK_BLOCK, "AIR"));
        assertEquals(CustomBlockAction.NONE,
                PlacedClickSupport.resolve(profile, IgnisInteraction.RIGHT_CLICK_BLOCK, "FLINT_AND_STEEL"));
    }

    @Test
    void leftClickUsesProfileAction() {
        StrategyProfile profile = StrategyProfile.builder()
                .leftClickAction(CustomBlockAction.BREAK)
                .build();

        assertEquals(CustomBlockAction.BREAK,
                PlacedClickSupport.resolve(profile, IgnisInteraction.LEFT_CLICK_BLOCK, "AIR"));
    }

    @Test
    void rightClickWithIgnitionItemIgnitesCombustibleBlocks() {
        StrategyProfile profile = StrategyProfile.builder()
                .combustible(true)
                .rightClickAction(CustomBlockAction.NONE)
                .ignitionMaterials(List.of("FLINT_AND_STEEL", "FIRE_CHARGE", "FLINT"))
                .build();

        assertEquals(CustomBlockAction.IGNITE,
                PlacedClickSupport.resolve(profile, IgnisInteraction.RIGHT_CLICK_BLOCK, "FLINT_AND_STEEL"));
    }

    @Test
    void nonCombustibleBlocksUseProfileRightClickAction() {
        StrategyProfile profile = StrategyProfile.builder()
                .combustible(false)
                .rightClickAction(CustomBlockAction.OPEN)
                .build();

        assertEquals(CustomBlockAction.OPEN,
                PlacedClickSupport.resolve(profile, IgnisInteraction.RIGHT_CLICK_BLOCK, "FLINT_AND_STEEL"));
    }

    @Test
    void customIgnitionMaterialsAreHonored() {
        StrategyProfile profile = StrategyProfile.builder()
                .combustible(true)
                .rightClickAction(CustomBlockAction.NONE)
                .ignitionMaterials(List.of("STICK"))
                .build();

        assertEquals(CustomBlockAction.IGNITE,
                PlacedClickSupport.resolve(profile, IgnisInteraction.RIGHT_CLICK_BLOCK, "STICK"));
        assertEquals(CustomBlockAction.NONE,
                PlacedClickSupport.resolve(profile, IgnisInteraction.RIGHT_CLICK_BLOCK, "AIR"));
    }

    @Test
    void airClicksIgnoreNonBlockInteractions() {
        StrategyProfile profile = StrategyProfile.defaults();

        assertEquals(CustomBlockAction.NONE,
                PlacedClickSupport.resolve(profile, IgnisInteraction.RIGHT_CLICK_AIR, "FLINT_AND_STEEL"));
    }

    @Test
    void detectsIgnitionMaterialsFromProfileOnly() {
        StrategyProfile profile = StrategyProfile.builder()
                .ignitionMaterials(List.of("STICK"))
                .build();

        assertTrue(PlacedClickSupport.isIgnitionMaterial(profile, "STICK"));
        assertFalse(PlacedClickSupport.isIgnitionMaterial(profile, "FLINT_AND_STEEL"));
        assertFalse(PlacedClickSupport.isIgnitionMaterial(profile, "STONE"));
    }
}
