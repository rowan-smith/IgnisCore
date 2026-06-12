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
                id: nuke
                name: Nuke Block
                version: 2.0.0
                api-version: 1.0.0
                author: Tester
                strategy: dev.rono.igniscore.block.nuke.Strategy
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("nuke", manifest.getId());
        assertEquals("Nuke Block", manifest.getName());
        assertEquals("2.0.0", manifest.getVersion());
        assertEquals("dev.rono.igniscore.block.nuke.Strategy", manifest.getStrategyClass());
        assertEquals("Tester", manifest.getAuthor());
        assertEquals("1.0.0", manifest.getApiVersion());
    }

    @Test
    void resolvesLegacyMainClassToStrategy() {
        String yaml = """
                id: nuke
                main: dev.rono.igniscore.block.nuke.BlockPlugin
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("dev.rono.igniscore.block.nuke.Strategy", manifest.getStrategyClass());
    }

    @Test
    void infersStrategyClassFromBlockExtensionId() {
        String yaml = """
                id: nuke
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("dev.rono.igniscore.block.nuke.Strategy", manifest.getStrategyClass());
    }

    @Test
    void infersStrategyClassFromItemExtensionId() {
        String yaml = """
                id: grenade
                """;

        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)),
                "item-extension.yml");

        assertEquals("dev.rono.igniscore.item.grenade.Strategy", manifest.getStrategyClass());
    }

    @Test
    void appliesDefaultsForMissingMetadata() {
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream("id: wormhole-tnt\n".getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        assertEquals("wormhole-tnt", manifest.getId());
        assertEquals("wormhole-tnt", manifest.getName());
        assertEquals("1.0.0", manifest.getVersion());
        assertEquals(IgnisApiVersion.CURRENT, manifest.getApiVersion());
        assertEquals("unknown", manifest.getAuthor());
        assertEquals("dev.rono.igniscore.block.wormholetnt.Strategy", manifest.getStrategyClass());
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
                        "unknown-manifest.yml"));

        assertEquals("extension manifest requires strategy", error.getMessage());
    }
}
