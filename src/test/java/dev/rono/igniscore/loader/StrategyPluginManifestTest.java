package dev.rono.igniscore.loader;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategyPluginManifestTest {
    @Test
    void parsesManifestFromYaml() {
        String yaml = """
                id: test-strategies
                name: Test Strategies
                version: 2.0.0
                api-version: 1.0.0
                author: Tester
                main: com.example.TestPlugin
                """;

        StrategyPluginManifest manifest = StrategyPluginManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));

        assertEquals("test-strategies", manifest.getId());
        assertEquals("Test Strategies", manifest.getName());
        assertEquals("2.0.0", manifest.getVersion());
        assertEquals("com.example.TestPlugin", manifest.getMainClass());
        assertEquals("Tester", manifest.getAuthor());
    }
}
