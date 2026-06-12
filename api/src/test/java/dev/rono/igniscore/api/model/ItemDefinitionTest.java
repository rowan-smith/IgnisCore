package dev.rono.igniscore.api.model;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDefinitionTest {
    @Test
    void exposesConfiguredItemMetadata() {
        ItemDefinition definition = new ItemDefinition(
                "grenade",
                "snowball",
                Component.text("Grenade"),
                List.of(Component.text("Throwable")),
                Map.of("power", 4.0),
                Map.of("right_click", Map.of("action", "throw")),
                20001,
                "grenade-item",
                "icon.png"
        );

        assertEquals("grenade", definition.getId());
        assertEquals("snowball", definition.getBaseMaterial());
        assertEquals(4.0, definition.getCustomData().get("power"));
        assertEquals("throw", ((Map<?, ?>) definition.getInteractionSettings().get("right_click")).get("action"));
        assertEquals(20001, definition.getCustomModelData());
        assertEquals("grenade-item", definition.getExtensionId());
        assertEquals("icon.png", definition.getIconTexture());
    }
}
