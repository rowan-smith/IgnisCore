package dev.rono.igniscore.api.model;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockDefinitionTest {
    @Test
    void renderMaterialFallsBackToBaseMaterial() {
        BlockDefinition definition = new BlockDefinition(
                "test",
                "paper",
                null,
                Component.text("Test"),
                List.of(),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false
        );

        assertEquals("paper", definition.getRenderMaterial());
    }

    @Test
    void extensionIdDefaultsToBuiltinWhenUsingShortConstructor() {
        BlockDefinition definition = new BlockDefinition(
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
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false
        );

        assertEquals("builtin", definition.getExtensionId());
    }

    @Test
    void perSideTexturesAreOptionalAndFallBackToSideTexture() {
        BlockDefinition definition = new BlockDefinition(
                "test",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(),
                true,
                true,
                "top.png",
                "shared-side.png",
                "bottom.png",
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false,
                "test-block",
                "north.png",
                null,
                "south.png",
                null
        );

        assertTrue(definition.hasPerSideTextures());
        assertEquals("north.png", definition.getSide1Texture());
        assertNull(definition.getSide2Texture());
        assertEquals("south.png", definition.getSide3Texture());
        assertNull(definition.getSide4Texture());
        assertEquals("north.png", definition.getResolvedSideTexture(1));
        assertEquals("shared-side.png", definition.getResolvedSideTexture(2));
        assertEquals("south.png", definition.getResolvedSideTexture(3));
        assertEquals("shared-side.png", definition.getResolvedSideTexture(4));
    }

    @Test
    void singleSideTextureDoesNotEnablePerSideMode() {
        BlockDefinition definition = new BlockDefinition(
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
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false
        );

        assertFalse(definition.hasPerSideTextures());
        assertEquals("side.png", definition.getResolvedSideTexture(1));
    }
}
