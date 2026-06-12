package dev.rono.igniscore.service;

import dev.rono.igniscore.api.CustomBlockAction;
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
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl();
        registry.register(IgnisStrategyDescriptor.of("custom", "Custom", "1.0.0", "test"),
                customStrategy());
        resolver = new StrategyProfileResolver(registry);
    }

    @Test
    void mergesDefinitionOverridesIntoStrategyProfile() {
        BlockDefinition definition = definition(Map.of(), Map.of("scale", 1.5), true, true, 90, 12.0);

        StrategyProfile profile = resolver.resolve(definition);

        assertEquals(90, profile.getDefaultFuse());
        assertEquals(12.0, profile.getDefaultRadius());
        assertEquals(1.5, profile.getDisplayScale());
        assertTrue(profile.isPlaceable());
        assertTrue(profile.isBreakable());
        assertEquals(CustomBlockAction.NONE, profile.getLeftClickAction());
    }

    @Test
    void marksProfileCombustibleWhenIgniteInteractionConfigured() {
        BlockDefinition definition = definition(
                Map.of("right_click", Map.of("action", "ignite")),
                Map.of(),
                true,
                true,
                80,
                4.0
        );

        StrategyProfile profile = resolver.resolve(definition);

        assertTrue(profile.isCombustible());
    }

    @Test
    void detectsIgniteFromMaterialActionsAndDedicatedSection() {
        BlockDefinition materialActionDefinition = definition(
                Map.of("left_click", Map.of(
                        "material_actions", List.of(Map.of("action", "ignite", "materials", List.of("STICK")))
                )),
                Map.of(),
                true,
                true,
                80,
                4.0
        );
        BlockDefinition dedicatedSectionDefinition = definition(
                Map.of("ignite", Map.of("sound", "ITEM_FLINTANDSTEEL_USE")),
                Map.of(),
                true,
                true,
                80,
                4.0
        );

        assertTrue(resolver.resolve(materialActionDefinition).isCombustible());
        assertTrue(resolver.resolve(dedicatedSectionDefinition).isCombustible());
    }

    @Test
    void rejectsNonBlockStrategies() {
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl();
        registry.register(IgnisStrategyDescriptor.of("item-only", "Item", "1.0.0", "test"),
                new dev.rono.igniscore.api.strategy.IgnisItemStrategy() {
                    @Override
                    public IgnisStrategyDescriptor descriptor() {
                        return IgnisStrategyDescriptor.of("item-only", "Item", "1.0.0", "test");
                    }
                });
        StrategyProfileResolver itemResolver = new StrategyProfileResolver(registry);

        BlockDefinition definition = definitionWithStrategy("item-only");

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> itemResolver.resolve(definition));
        assertEquals("Block type test uses a non-block strategy: item-only", error.getMessage());
    }

    private IgnisBlockStrategy customStrategy() {
        return new IgnisBlockStrategy() {
            @Override
            public IgnisStrategyDescriptor descriptor() {
                return IgnisStrategyDescriptor.of("custom", "Custom", "1.0.0", "test");
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

    private BlockDefinition definitionWithStrategy(String strategy) {
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
                strategy,
                Map.of("fuse", 80, "radius", 4.0),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false
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
                "custom",
                Map.of("fuse", fuse, "radius", radius),
                Map.of(),
                interactions,
                displaySettings,
                10001,
                false,
                false,
                false
        );
    }
}
