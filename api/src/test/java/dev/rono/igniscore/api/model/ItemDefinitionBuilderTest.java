package dev.rono.igniscore.api.model;

import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDefinitionBuilderTest {
    @Test
    void builderExposesCustomAndInteractionConfig() {
        ItemDefinition definition = ItemDefinition.builder("detonator")
                .baseMaterial("blaze_rod")
                .title(Component.text("Detonator"))
                .description(List.of(Component.text("Links charges")))
                .customData(Map.of("max_links", 16))
                .behaviorSettings(Map.of(
                        "left_click_block", "assign",
                        "right_click_air", "detonate",
                        "right_click_block", "detonate"))
                .interactionSettings(Map.of())
                .customModelData(20002)
                .iconTexture("icon.png")
                .build();

        assertEquals("detonator", definition.getId());
        assertEquals(16, definition.getCustomConfig().getInt("max_links", 0));
        assertEquals("assign", definition.getBehaviorConfig().getString("left_click_block", ""));
    }
}
