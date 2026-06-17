package dev.rono.extensions.shared;

import dev.rono.igniscore.api.model.ItemDefinition;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExtensionSharedTest {
    @Test
    void configFacadeDelegatesToExtensionConfigs() {
        ItemDefinition definition = new ItemDefinition(
                "test",
                "snowball",
                Component.text("Test"),
                List.of(),
                Map.of("throw_velocity", 2.0, "fuse_ticks", 30),
                Map.of(),
                Map.of(),
                10001,
                "test",
                "icon.png");

        assertEquals(2.0, ExtensionShared.config().throwable(definition).throwVelocity());
        assertEquals(30, ExtensionShared.config().throwable(definition).fuseTicks());
    }
}
