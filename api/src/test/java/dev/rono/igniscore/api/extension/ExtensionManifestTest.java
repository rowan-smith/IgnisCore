package dev.rono.igniscore.api.extension;

import dev.rono.igniscore.api.IgnisApiVersion;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ExtensionManifestTest {
    @Test
    void parsesStrategyManifestFromYaml() {
        String yaml = """
                id: nuclear-block
                name: Nuclear Block
                version: 2.0.0
                api-version: 1.0.0
                author: Tester
                strategy: dev.rono.blocks.nuclear.Strategy
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("nuclear-block", manifest.getId());
        assertEquals("Nuclear Block", manifest.getName());
        assertEquals("2.0.0", manifest.getVersion());
        assertEquals("dev.rono.blocks.nuclear.Strategy", manifest.getStrategyClass());
        assertEquals("Tester", manifest.getAuthor());
        assertEquals("1.0.0", manifest.getApiVersion());
    }

    @Test
    void resolvesLegacyMainClassToStrategy() {
        String yaml = """
                id: nuclear-block
                main: dev.rono.blocks.nuclear.BlockPlugin
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("dev.rono.blocks.nuclear.Strategy", manifest.getStrategyClass());
    }

    @Test
    void infersStrategyClassFromBlockExtensionId() {
        String yaml = """
                id: nuclear-block
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("dev.rono.blocks.nuclear.Strategy", manifest.getStrategyClass());
    }

    @Test
    void infersStrategyClassFromItemExtensionId() {
        String yaml = """
                id: grenade-item
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "item-extension.yml");

        assertEquals("dev.rono.items.grenade.Strategy", manifest.getStrategyClass());
    }

    @Test
    void appliesDefaultsForMissingMetadata() {
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream("id: wormhole-block\n".getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("wormhole-block", manifest.getId());
        assertEquals("wormhole-block", manifest.getName());
        assertEquals("1.0.0", manifest.getVersion());
        assertEquals(IgnisApiVersion.CURRENT, manifest.getApiVersion());
        assertEquals("unknown", manifest.getAuthor());
        assertEquals("dev.rono.blocks.wormhole.Strategy", manifest.getStrategyClass());
    }

    @Test
    void requiresExtensionId() {
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> ExtensionManifest.fromStream(
                        new ByteArrayInputStream("name: Missing Id\n".getBytes(StandardCharsets.UTF_8)),
                        "block-extension.yml"));

        assertEquals("block-extension.yml requires id", error.getMessage());
    }

    @Test
    void requiresResolvableStrategyClass() {
        NullPointerException error = assertThrows(NullPointerException.class,
                () -> ExtensionManifest.fromStream(
                        new ByteArrayInputStream("id: custom-extension\n".getBytes(StandardCharsets.UTF_8)),
                        "block-extension.yml"));

        assertEquals("extension manifest requires strategy", error.getMessage());
    }
}
