package dev.rono.igniscore.api.extension;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockExtensionManifestTest {
    @Test
    void parsesManifestFromYaml() {
        String yaml = """
                id: nuclear-block
                name: Nuclear Block
                version: 2.0.0
                api-version: 1.0.0
                author: Tester
                main: dev.rono.blocks.nuclear.BlockPlugin
                """;

        BlockExtensionManifest manifest = BlockExtensionManifest.fromStream(
                new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)));

        assertEquals("nuclear-block", manifest.getId());
        assertEquals("Nuclear Block", manifest.getName());
        assertEquals("2.0.0", manifest.getVersion());
        assertEquals("dev.rono.blocks.nuclear.BlockPlugin", manifest.getMainClass());
        assertEquals("Tester", manifest.getAuthor());
    }
}
