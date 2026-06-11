package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefinitionParserTest {
    @Test
    void buildsStrategyDescriptorFromConfigAndManifest() {
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream("""
                        id: nuclear-block
                        name: Nuclear Block
                        version: 2.0.0
                        author: IgnisCore
                        """.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(new java.io.StringReader("""
                behavior:
                  strategy: nuclear
                  strategy_name: Nuclear Detonation
                """));

        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(config, manifest);

        assertEquals("nuclear", descriptor.getId());
        assertEquals("Nuclear Detonation", descriptor.getName());
        assertEquals("2.0.0", descriptor.getVersion());
        assertEquals("IgnisCore", descriptor.getAuthor());
        assertEquals("nuclear-block", descriptor.getSourcePlugin());
    }

    @Test
    void parsesBlockDefinitionWithDefaultsAndNestedSections() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new java.io.StringReader("""
                id: phantom
                display:
                  title: "&5Phantom"
                  description:
                    - "&7Vanishes on trigger"
                block:
                  placeable: false
                  breakable: false
                  base_material: PAPER
                  render_material: CARROT_ON_A_STICK
                  breaking:
                    ticks: 30
                textures:
                  top: custom-top.png
                  side: custom-side.png
                  bottom: custom-bottom.png
                behavior:
                  strategy: phantom
                  fuse: 40
                  radius: 6.5
                  custom_data:
                    power: 3.0
                interactions:
                  right_click:
                    action: ignite
                block_display:
                  scale: 1.25
                  animations:
                    rotate: false
                    float: false
                    pulse: false
                """));

        BlockDefinition definition = DefinitionParser.parseBlock(config, "fallback", 10042, "phantom-block");

        assertEquals("phantom", definition.getId());
        assertEquals("paper", definition.getBaseMaterial());
        assertEquals("carrot_on_a_stick", definition.getRenderMaterial());
        assertEquals("Phantom", PlainTextComponentSerializer.plainText().serialize(definition.getTitle()));
        assertFalse(definition.isPlaceable());
        assertFalse(definition.isBreakable());
        assertEquals("custom-top.png", definition.getTopTexture());
        assertEquals("phantom", definition.getStrategy());
        assertEquals(40, definition.getFuse());
        assertEquals(6.5, definition.getRadius());
        assertEquals(3.0, definition.getCustomData().get("power"));
        assertEquals(30, definition.getBreakSettings().get("ticks"));
        assertEquals("ignite", ((Map<?, ?>) definition.getInteractionSettings().get("right_click")).get("action"));
        assertEquals(1.25, definition.getDisplaySettings().get("scale"));
        assertFalse(definition.isRotate());
        assertFalse(definition.isFloatBob());
        assertFalse(definition.isPulse());
        assertEquals(10042, definition.getCustomModelData());
        assertEquals("phantom-block", definition.getExtensionId());
    }

    @Test
    void mapsLegacyExplosionSectionIntoBehaviorFields() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new java.io.StringReader("""
                id: legacy
                behavior:
                  strategy: default
                explosion:
                  strategy: erupting
                  fuse: 55
                  radius: 9.0
                  power: 7.5
                  multiplier: 2.0
                  effects:
                    fire: true
                    destroy_blocks: false
                    screen_shake: true
                  entity_payload:
                    type: PRIMED_TNT
                    count: 3
                    behavior: scatter
                    target_players: true
                """));

        BlockDefinition definition = DefinitionParser.parseBlock(config, "legacy", 10001, "legacy-block");

        assertEquals("erupting", definition.getStrategy());
        assertEquals(55, definition.getFuse());
        assertEquals(9.0, definition.getRadius());
        assertEquals(7.5, definition.getCustomData().get("power"));
        assertEquals(2.0, definition.getCustomData().get("multiplier"));
        assertEquals(true, definition.getCustomData().get("fire"));
        assertEquals(false, definition.getCustomData().get("blockDamage"));
        assertEquals(true, definition.getCustomData().get("screenShake"));

        @SuppressWarnings("unchecked")
        Map<String, Object> payload = (Map<String, Object>) definition.getCustomData().get("entityPayload");
        assertEquals("PRIMED_TNT", payload.get("type"));
        assertEquals(3, payload.get("count"));
        assertEquals("scatter", payload.get("behavior"));
        assertEquals(true, payload.get("targetPlayers"));
    }

    @Test
    void preservesExplicitBehaviorValuesOverLegacyExplosionDefaults() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new java.io.StringReader("""
                behavior:
                  strategy: nuclear
                  fuse: 120
                  radius: 20.0
                explosion:
                  strategy: erupting
                  fuse: 10
                  radius: 2.0
                """));

        BlockDefinition definition = DefinitionParser.parseBlock(config, "nuke", 10001, "nuclear-block");

        assertEquals("nuclear", definition.getStrategy());
        assertEquals(120, definition.getFuse());
        assertEquals(20.0, definition.getRadius());
    }

    @Test
    void parsesItemDefinition() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new java.io.StringReader("""
                id: grenade
                display:
                  title: "&cGrenade"
                  description:
                    - "&7Throwable"
                item:
                  base_material: SNOWBALL
                textures:
                  icon: grenade-icon.png
                behavior:
                  strategy: grenade
                  custom_data:
                    power: 4.0
                    fuse_ticks: 40
                interactions:
                  right_click:
                    action: throw
                """));

        ItemDefinition definition = DefinitionParser.parseItem(config, "fallback-item", 20005, "grenade-item");

        assertEquals("grenade", definition.getId());
        assertEquals("snowball", definition.getBaseMaterial());
        assertEquals("Grenade", PlainTextComponentSerializer.plainText().serialize(definition.getTitle()));
        assertEquals("grenade", definition.getStrategy());
        assertEquals(4.0, definition.getCustomData().get("power"));
        assertEquals(40, definition.getCustomData().get("fuse_ticks"));
        assertEquals("throw", ((Map<?, ?>) definition.getInteractionSettings().get("right_click")).get("action"));
        assertEquals(20005, definition.getCustomModelData());
        assertEquals("grenade-item", definition.getExtensionId());
        assertEquals("grenade-icon.png", definition.getIconTexture());
    }

    @Test
    void parsesRealNuclearBlockConfigFixture() throws Exception {
        try (var input = getClass().getResourceAsStream("/fixtures/nuclear-block-config.yml")) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(new java.io.StringReader(
                    new String(input.readAllBytes(), StandardCharsets.UTF_8)));

            BlockDefinition definition = DefinitionParser.parseBlock(config, "nuke", 10001, "nuclear-block");

            assertEquals("nuke", definition.getId());
            assertEquals("nuclear", definition.getStrategy());
            assertEquals(160, definition.getFuse());
            assertEquals(30.0, definition.getRadius());
            assertEquals(30.0, definition.getCustomData().get("power"));
            assertTrue(definition.isPlaceable());
            assertTrue(definition.isBreakable());
            assertTrue(definition.getInteractionSettings().isEmpty());
        }
    }
}
