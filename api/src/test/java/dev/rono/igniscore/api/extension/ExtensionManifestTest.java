package dev.rono.igniscore.api.extension;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
