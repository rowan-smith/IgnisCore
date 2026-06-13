package dev.rono.igniscore.api.model;

import dev.rono.igniscore.api.port.IgnisInteraction;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockDefinitionBuilderTest {
    @Test
    void builderProducesEquivalentDefinition() {
        BlockDefinition built = BlockDefinition.builder("cache")
                .baseMaterial("paper")
                .renderMaterial("carrot_on_a_stick")
                .title(Component.text("Cache"))
                .description(List.of(Component.text("Stores drops")))
                .placeable(true)
                .breakable(false)
                .textures("top.png", "side.png", "bottom.png")
                .customData(Map.of("capacity", 64))
                .breakSettings(Map.of("ticks", 10))
                .interactionSettings(Map.of("right_click", Map.of("action", "open")))
                .customModelData(10042)
                .extensionId("quarry-cache")
                .animations(false, false, false)
                .build();

        assertEquals("cache", built.getId());
        assertEquals("quarry-cache", built.getExtensionId());
        assertFalse(built.isBreakable());
        assertEquals(64, built.getCustomConfig().getInt("capacity", 0));
        assertEquals(10, built.getBreakConfig().getInt("ticks", 0));
        assertEquals("open", built.getInteractionConfig().section("right_click").getString("action", ""));
        assertFalse(built.isRotate());
    }
}
