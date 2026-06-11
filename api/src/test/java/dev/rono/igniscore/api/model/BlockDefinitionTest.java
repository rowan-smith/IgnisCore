package dev.rono.igniscore.api.model;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                "default",
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
                "default",
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
}
