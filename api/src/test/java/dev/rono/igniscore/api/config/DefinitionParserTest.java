package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefinitionParserTest {
    @Test
    void buildsStrategyDescriptorFromManifest() {
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream("""
                        id: nuke
                        name: Nuke Block
                        version: 2.0.0
                        author: IgnisCore
                        """.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(manifest);

        assertEquals("nuke", descriptor.getId());
        assertEquals("Nuke Block", descriptor.getName());
        assertEquals("2.0.0", descriptor.getVersion());
        assertEquals("IgnisCore", descriptor.getAuthor());
        assertEquals("nuke", descriptor.getSourcePlugin());
    }

    @Test
    void parsesBlockDefinitionWithDefaultsAndNestedSections() {
        BlockDefinition definition = DefinitionParser.parseBlock(yaml("""
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
                custom_data:
                  fuse: 40
                  radius: 6.5
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
                """), "fallback", 10042, "phantom-tnt");

        assertEquals("phantom", definition.getId());
        assertEquals("paper", definition.getBaseMaterial());
        assertEquals("carrot_on_a_stick", definition.getRenderMaterial());
        assertEquals("Phantom", PlainTextComponentSerializer.plainText().serialize(definition.getTitle()));
        assertFalse(definition.isPlaceable());
        assertFalse(definition.isBreakable());
        assertEquals("custom-top.png", definition.getTopTexture());
        assertEquals(40, definition.getCustomData().get("fuse"));
        assertEquals(6.5, definition.getCustomData().get("radius"));
        assertEquals(3.0, definition.getCustomData().get("power"));
        assertEquals(30, definition.getBreakSettings().get("ticks"));
        assertEquals("ignite", ((Map<?, ?>) definition.getInteractionSettings().get("right_click")).get("action"));
        assertEquals(1.25, definition.getDisplaySettings().get("scale"));
        assertFalse(definition.isRotate());
        assertFalse(definition.isFloatBob());
        assertFalse(definition.isPulse());
        assertEquals(10042, definition.getCustomModelData());
        assertEquals("phantom-tnt", definition.getExtensionId());
    }

    @Test
    void parsesBehaviorSectionForBlocks() {
        BlockDefinition definition = DefinitionParser.parseBlock(yaml("""
                id: cache
                behavior:
                  combustible: false
                  left_click_block: break
                  right_click_block: open
                  sounds:
                    place: BLOCK_CHEST_PLACE
                custom_data:
                  capacity: 64
                """), "cache", 10001, "quarry-cache");

        assertEquals("break", definition.getBehaviorConfig().getString("left_click_block", ""));
        assertEquals("open", definition.getBehaviorConfig().getString("right_click_block", ""));
        assertEquals("BLOCK_CHEST_PLACE", definition.getBehaviorConfig().section("sounds").getString("place", ""));
        assertEquals(64, definition.getCustomData().get("capacity"));
    }

    @Test
    void parsesItemDefinition() {
        ItemDefinition definition = DefinitionParser.parseItem(yaml("""
                id: grenade
                display:
                  title: "&cGrenade"
                  description:
                    - "&7Throwable"
                item:
                  base_material: SNOWBALL
                textures:
                  icon: grenade-icon.png
                custom_data:
                  power: 4.0
                  fuse_ticks: 40
                behavior:
                  right_click_air: throw
                  right_click_block: throw
                """), "fallback-item", 20005, "grenade");

        assertEquals("grenade", definition.getId());
        assertEquals("snowball", definition.getBaseMaterial());
        assertEquals("Grenade", PlainTextComponentSerializer.plainText().serialize(definition.getTitle()));
        assertEquals(4.0, definition.getCustomData().get("power"));
        assertEquals(40, definition.getCustomData().get("fuse_ticks"));
        assertEquals("throw", definition.getBehaviorConfig().getString("right_click_air", ""));
        assertEquals("throw", definition.getBehaviorConfig().getString("right_click_block", ""));
        assertEquals(20005, definition.getCustomModelData());
        assertEquals("grenade", definition.getExtensionId());
        assertEquals("grenade-icon.png", definition.getIconTexture());
    }

    @Test
    void parsesPerSideTexturesWithFallbackToSharedSide() {
        BlockDefinition definition = DefinitionParser.parseBlock(yaml("""
                id: custom
                textures:
                  top: top.png
                  side: shared.png
                  side-1: north.png
                  side-3: south.png
                """), "fallback", 10001, "custom-block");

        assertTrue(definition.hasPerSideTextures());
        assertEquals("north.png", definition.getSide1Texture());
        assertNull(definition.getSide2Texture());
        assertEquals("south.png", definition.getSide3Texture());
        assertNull(definition.getSide4Texture());
        assertEquals("shared.png", definition.getResolvedSideTexture(2));
        assertEquals("shared.png", definition.getResolvedSideTexture(4));
    }

    @Test
    void parsesAllPerSideTextures() {
        BlockDefinition definition = DefinitionParser.parseBlock(yaml("""
                id: custom
                textures:
                  top: top.png
                  side: fallback.png
                  side-1: north.png
                  side-2: east.png
                  side-3: south.png
                  side-4: west.png
                  bottom: bottom.png
                """), "fallback", 10001, "custom-block");

        assertEquals("north.png", definition.getSide1Texture());
        assertEquals("east.png", definition.getSide2Texture());
        assertEquals("south.png", definition.getSide3Texture());
        assertEquals("west.png", definition.getSide4Texture());
    }

    @Test
    void parsesJpegTextureFilenames() {
        BlockDefinition definition = DefinitionParser.parseBlock(yaml("""
                id: custom
                textures:
                  top: top.jpg
                  side: side.jpeg
                  bottom: bottom.jpg
                  side-1: north.jpg
                """), "fallback", 10001, "custom-block");

        assertEquals("top.jpg", definition.getTopTexture());
        assertEquals("side.jpeg", definition.getSideTexture());
        assertEquals("bottom.jpg", definition.getBottomTexture());
        assertEquals("north.jpg", definition.getSide1Texture());
    }

    @Test
    void singleSideTextureKeepsLegacyMode() {
        BlockDefinition definition = DefinitionParser.parseBlock(yaml("""
                id: custom
                textures:
                  top: top.png
                  side: side.png
                  bottom: bottom.png
                """), "fallback", 10001, "custom-block");

        assertFalse(definition.hasPerSideTextures());
        assertNull(definition.getSide1Texture());
    }

    private static Map<String, Object> yaml(String content) {
        return YamlDefinitions.loadMap(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }
}

