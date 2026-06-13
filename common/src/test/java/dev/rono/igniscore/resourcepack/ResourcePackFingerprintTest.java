package dev.rono.igniscore.resourcepack;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import net.kyori.adventure.text.Component;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourcePackFingerprintTest {
    @Test
    void changesWhenBlockTextureChanges() throws Exception {
        BlockDefinition original = blockDefinition("top-a.png");
        BlockDefinition updated = blockDefinition("top-b.png");

        String before = ResourcePackFingerprint.compute(
                Map.of(original.getId(), original),
                Map.of(),
                List.of(),
                List.of());
        String after = ResourcePackFingerprint.compute(
                Map.of(updated.getId(), updated),
                Map.of(),
                List.of(),
                List.of());

        assertNotEquals(before, after);
    }

    @Test
    void includesJarModificationTime(@TempDir Path tempDir) throws Exception {
        BlockDefinition definition = blockDefinition("top.png");
        File jarFile = tempDir.resolve("extension.jar").toFile();
        java.nio.file.Files.writeString(jarFile.toPath(), "demo");
        var extension = extensionWithJar(definition, jarFile);

        String first = ResourcePackFingerprint.compute(
                Map.of(definition.getId(), definition),
                Map.of(),
                List.of(extension),
                List.of());

        long originalModified = jarFile.lastModified();
        assertTrue(jarFile.setLastModified(originalModified + 5_000));

        String second = ResourcePackFingerprint.compute(
                Map.of(definition.getId(), definition),
                Map.of(),
                List.of(extension),
                List.of());

        jarFile.setLastModified(originalModified);
        assertNotEquals(first, second);
    }

    private static dev.rono.igniscore.loader.LoadedExtension<BlockDefinition> extensionWithJar(
            BlockDefinition definition, File jarFile) throws Exception {
        return new dev.rono.igniscore.loader.LoadedExtension<>(
                dev.rono.igniscore.api.extension.ExtensionManifest.fromStream(
                        new java.io.ByteArrayInputStream("""
                                id: test-block
                                name: Test
                                version: 1.0.0
                                api-version: 1.0.0
                                strategy: dev.example.Strategy
                                """.getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                        "block-extension.yml"),
                jarFile,
                new java.net.URLClassLoader(new java.net.URL[]{jarFile.toURI().toURL()}),
                definition,
                new dev.rono.igniscore.api.extension.ExtensionResources(
                        new java.net.URLClassLoader(new java.net.URL[]{jarFile.toURI().toURL()})));
    }

    @Test
    void isStableForIdenticalInputs() throws Exception {
        BlockDefinition definition = blockDefinition("top.png");
        ItemDefinition item = ItemDefinition.builder("grenade").build();
        Map<String, BlockDefinition> blocks = Map.of(definition.getId(), definition);
        Map<String, ItemDefinition> items = Map.of(item.getId(), item);

        String first = ResourcePackFingerprint.compute(blocks, items, List.of(), List.of());
        String second = ResourcePackFingerprint.compute(blocks, items, List.of(), List.of());

        assertEquals(first, second);
    }

    private static BlockDefinition blockDefinition(String topTexture) {
        return new BlockDefinition(
                "test-block",
                "paper",
                "carrot_on_a_stick",
                Component.text("Test"),
                List.of(),
                true,
                true,
                topTexture,
                "side.png",
                "bottom.png",
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false,
                "test-block");
    }
}
