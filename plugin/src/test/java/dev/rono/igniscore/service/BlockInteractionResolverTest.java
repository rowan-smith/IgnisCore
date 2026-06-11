package dev.rono.igniscore.service;

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import dev.rono.igniscore.core.IgnisStrategyRegistryImpl;
import dev.rono.igniscore.api.model.BlockDefinition;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockInteractionResolverTest {
    private BlockInteractionResolver resolver;

    @BeforeEach
    void setUp() {
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl();
        resolver = new BlockInteractionResolver(new StrategyProfileResolver(registry));
    }

    @Test
    void leftClickDefaultsToBreak() {
        BlockDefinition definition = definitionWithInteractions(Map.of());

        assertEquals(CustomBlockAction.BREAK,
                resolver.resolve(definition, Action.LEFT_CLICK_BLOCK, (Material) null));
    }

    @Test
    void rightClickWithDefaultIgnitionItemIgnites() {
        BlockDefinition definition = definitionWithInteractions(Map.of());

        assertEquals(CustomBlockAction.IGNITE,
                resolver.resolve(definition, Action.RIGHT_CLICK_BLOCK, Material.FLINT_AND_STEEL));
    }

    @Test
    void configuredIgniteRequiresMatchingMaterial() {
        BlockDefinition definition = definitionWithInteractions(Map.of(
                "right_click", Map.of(
                        "action", "ignite",
                        "materials", List.of("FLINT_AND_STEEL")
                )
        ));

        assertEquals(CustomBlockAction.IGNITE,
                resolver.resolve(definition, Action.RIGHT_CLICK_BLOCK, Material.FLINT_AND_STEEL));
        assertEquals(CustomBlockAction.NONE,
                resolver.resolve(definition, Action.RIGHT_CLICK_BLOCK, Material.STICK));
    }

    @Test
    void leftClickIgnitesViaMaterialAction() {
        BlockDefinition definition = definitionWithInteractions(Map.of(
                "left_click", Map.of(
                        "material_actions", List.of(Map.of(
                                "materials", List.of("FIRE_CHARGE"),
                                "action", "ignite"
                        ))
                )
        ));

        assertEquals(CustomBlockAction.IGNITE,
                resolver.resolve(definition, Action.LEFT_CLICK_BLOCK, Material.FIRE_CHARGE));
        assertEquals(CustomBlockAction.BREAK,
                resolver.resolve(definition, Action.LEFT_CLICK_BLOCK, Material.STICK));
    }

    @Test
    void emptyHandDoesNotIgniteWhenMaterialsRequired() {
        BlockDefinition definition = definitionWithInteractions(Map.of(
                "right_click", Map.of(
                        "action", "ignite",
                        "materials", List.of("FLINT_AND_STEEL")
                )
        ));

        assertEquals(CustomBlockAction.NONE,
                resolver.resolve(definition, Action.RIGHT_CLICK_BLOCK, (Material) null));
    }

    @Test
    void strategyProfileFallbackHonorsNonCombustibleBlocks() {
        IgnisStrategyRegistryImpl registry = new IgnisStrategyRegistryImpl();
        registry.register(IgnisStrategyDescriptor.of("inert", "Inert", "1.0.0", "test"),
                new dev.rono.igniscore.api.strategy.IgnisBlockStrategy() {
                    @Override
                    public dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor descriptor() {
                        return IgnisStrategyDescriptor.of("inert", "Inert", "1.0.0", "test");
                    }

                    @Override
                    public dev.rono.igniscore.api.strategy.StrategyProfile profile(BlockDefinition definition) {
                        return dev.rono.igniscore.api.strategy.StrategyProfile.builder()
                                .combustible(false)
                                .leftClickAction(CustomBlockAction.NONE)
                                .rightClickAction(CustomBlockAction.BREAK)
                                .build();
                    }
                });
        BlockInteractionResolver inertResolver = new BlockInteractionResolver(new StrategyProfileResolver(registry));
        BlockDefinition definition = definitionWithInteractions(Map.of());
        definition = new BlockDefinition(
                definition.getId(),
                definition.getBaseMaterial(),
                definition.getRenderMaterial(),
                definition.getTitle(),
                definition.getDescription(),
                definition.isPlaceable(),
                definition.isBreakable(),
                definition.getTopTexture(),
                definition.getSideTexture(),
                definition.getBottomTexture(),
                "inert",
                definition.getCustomData(),
                definition.getBreakSettings(),
                definition.getInteractionSettings(),
                definition.getDisplaySettings(),
                definition.getCustomModelData(),
                definition.isRotate(),
                definition.isFloatBob(),
                definition.isPulse(),
                definition.getExtensionId()
        );

        assertEquals(CustomBlockAction.NONE,
                inertResolver.resolve(definition, Action.LEFT_CLICK_BLOCK, Material.FLINT_AND_STEEL));
        assertEquals(CustomBlockAction.BREAK,
                inertResolver.resolve(definition, Action.RIGHT_CLICK_BLOCK, Material.STICK));
    }

    @Test
    void rightClickOpenActionResolves() {
        BlockDefinition definition = definitionWithInteractions(Map.of(
                "right_click", Map.of(
                        "action", "open"
                )
        ));

        assertEquals(CustomBlockAction.OPEN,
                resolver.resolve(definition, Action.RIGHT_CLICK_BLOCK, Material.STICK));
    }

    @Test
    void materialActionsOverrideDefaultAction() {
        BlockDefinition definition = definitionWithInteractions(Map.of(
                "right_click", Map.of(
                        "default_action", "break",
                        "material_actions", List.of(Map.of(
                                "materials", List.of("FIRE_CHARGE"),
                                "action", "ignite"
                        ))
                )
        ));

        assertEquals(CustomBlockAction.IGNITE,
                resolver.resolve(definition, Action.RIGHT_CLICK_BLOCK, Material.FIRE_CHARGE));
        assertEquals(CustomBlockAction.BREAK,
                resolver.resolve(definition, Action.RIGHT_CLICK_BLOCK, Material.STICK));
    }

    private BlockDefinition definitionWithInteractions(Map<String, Object> interactions) {
        return new BlockDefinition(
                "test",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(Component.text("Description")),
                true,
                true,
                "phantom-tnt-erupting-tnt-mimic-tnt-wormhole-tnt-top.png",
                "phantom-tnt-erupting-tnt-mimic-tnt-wormhole-tnt-side.png",
                "phantom-tnt-erupting-tnt-mimic-tnt-wormhole-tnt-bottom.png",
                "default",
                Map.of("fuse", 40, "radius", 4.0),
                Map.of(),
                interactions,
                Map.of(),
                10001,
                false,
                false,
                false
        );
    }
}
