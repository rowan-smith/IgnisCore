package dev.rono.igniscore.api.model;

import dev.rono.igniscore.api.port.IgnisInteraction;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDefinitionBuilderTest {
    @Test
    void builderAndInteractionActionExposeConfiguredClicks() {
        ItemDefinition definition = ItemDefinition.builder("detonator")
                .baseMaterial("blaze_rod")
                .title(Component.text("Detonator"))
                .description(List.of(Component.text("Links charges")))
                .customData(Map.of("max_links", 16))
                .interactionSettings(Map.of(
                        "left_click", Map.of("action", "assign_bomb"),
                        "right_click", Map.of("action", "detonate_linked")
                ))
                .customModelData(20002)
                .iconTexture("icon.png")
                .build();

        assertEquals("detonator", definition.getId());
        assertEquals(16, definition.getCustomConfig().getInt("max_links", 0));
        assertEquals("assign_bomb", definition.interactionAction(IgnisInteraction.LEFT_CLICK_BLOCK));
        assertEquals("detonate_linked", definition.interactionAction(IgnisInteraction.RIGHT_CLICK_AIR));
    }
}
