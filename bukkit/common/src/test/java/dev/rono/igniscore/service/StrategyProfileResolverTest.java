package dev.rono.igniscore.service;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.support.TestIgnisCore;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.IgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StrategyProfileResolverTest {
    private StrategyProfileResolver resolver;

    @BeforeEach
    void setUp() {
        IgnisStrategyRegistryImpl registry = TestIgnisCore.newStrategyRegistry();
        registry.register(IgnisStrategyDescriptor.of("custom", "Custom", "1.0.0", "custom"),
                customStrategy());
        resolver = new StrategyProfileResolver(registry);
    }

    @Test
    void mergesDefinitionOverridesIntoStrategyProfile() {
        BlockDefinition definition = definition(Map.of(), Map.of("scale", 1.5), true, true, 90, 12.0);

        StrategyProfile profile = resolver.resolve(definition);

        assertTrue(profile.hasFuseLifecycle());
        assertEquals(90, profile.getDefaultFuse());
        assertTrue(profile.hasExplosionRadius());
        assertEquals(12.0, profile.getDefaultRadius());
        assertEquals(1.5, profile.getDisplayScale());
        assertTrue(profile.isPlaceable());
        assertTrue(profile.isBreakable());
        assertEquals(CustomBlockAction.NONE, profile.getLeftClickAction());
        assertFalse(profile.isCombustible());
    }

    @Test
    void ignoresFuseWhenCustomDataDoesNotDeclareIt() {
        BlockDefinition definition = definitionWithoutFuse(Map.of("scale", 1.5), true, true);

        StrategyProfile profile = resolver.resolve(definition);

        assertFalse(profile.hasFuseLifecycle());
        assertFalse(profile.hasExplosionRadius());
        assertEquals(0, profile.getDefaultFuse());
        assertEquals(0.0, profile.getDefaultRadius());
    }

    @Test
    void appliesFuseFromCustomDataForRemoteDetonationBlocks() {
        BlockDefinition definition = new BlockDefinition(
                "test",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(Component.text("Description")),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of("fuse", 0, "radius", 6.0),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false,
                "custom"
        );

        StrategyProfile profile = resolver.resolve(definition);

        assertTrue(profile.hasFuseLifecycle());
        assertEquals(0, profile.getDefaultFuse());
        assertTrue(profile.hasExplosionRadius());
        assertEquals(6.0, profile.getDefaultRadius());
    }

    @Test
    void rejectsNonBlockStrategies() {
        IgnisStrategyRegistryImpl registry = TestIgnisCore.newStrategyRegistry();
        registry.register(IgnisStrategyDescriptor.of("item-only", "Item", "1.0.0", "item-only"),
                new dev.rono.igniscore.api.strategy.IgnisItemStrategy() {
                    @Override
                    public IgnisStrategyDescriptor descriptor() {
                        return IgnisStrategyDescriptor.of("item-only", "Item", "1.0.0", "item-only");
                    }
                });
        StrategyProfileResolver itemResolver = new StrategyProfileResolver(registry);

        BlockDefinition definition = definitionWithExtensionId("item-only");

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> itemResolver.resolve(definition));
        assertEquals("Block type test uses a non-block strategy from extension item-only", error.getMessage());
    }

    private IgnisBlockStrategy customStrategy() {
        return new IgnisBlockStrategy() {
            @Override
            public IgnisStrategyDescriptor descriptor() {
                return IgnisStrategyDescriptor.of("custom", "Custom", "1.0.0", "custom");
            }

            @Override
            public StrategyProfile profile(BlockDefinition definition) {
                return StrategyProfile.builder()
                        .combustible(false)
                        .leftClickAction(CustomBlockAction.NONE)
                        .rightClickAction(CustomBlockAction.BREAK)
                        .build();
            }
        };
    }

    private BlockDefinition definitionWithExtensionId(String extensionId) {
        return new BlockDefinition(
                "test",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(Component.text("Description")),
                true,
                true,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of("fuse", 80, "radius", 4.0),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false,
                extensionId
        );
    }

    private BlockDefinition definitionWithoutFuse(Map<String, Object> displaySettings,
                                                  boolean placeable,
                                                  boolean breakable) {
        return new BlockDefinition(
                "test",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(Component.text("Description")),
                placeable,
                breakable,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of(),
                Map.of(),
                Map.of(),
                displaySettings,
                10001,
                false,
                false,
                false,
                "custom"
        );
    }

    private BlockDefinition definition(Map<String, Object> interactions,
                                       Map<String, Object> displaySettings,
                                       boolean placeable,
                                       boolean breakable,
                                       int fuse,
                                       double radius) {
        return new BlockDefinition(
                "test",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(Component.text("Description")),
                placeable,
                breakable,
                "top.png",
                "side.png",
                "bottom.png",
                Map.of("fuse", fuse, "radius", radius),
                Map.of(),
                interactions,
                displaySettings,
                10001,
                false,
                false,
                false,
                "custom"
        );
    }
}
