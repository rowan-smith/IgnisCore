package dev.rono.igniscore.loader;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.extension.ExtensionResources;
import dev.rono.igniscore.support.TestDefinitions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URLClassLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class LoadedExtensionTest {
    @Test
    void exposesManifestJarDefinitionAndResources() throws Exception {
        try (URLClassLoader classLoader = new URLClassLoader(new java.net.URL[0], getClass().getClassLoader())) {
            var definition = TestDefinitions.block("nuke");
            ExtensionManifest manifest = ExtensionManifest.fromStream(
                    new java.io.ByteArrayInputStream("id: nuke\n".getBytes()),
                    "block-extension.yml");
            File jarFile = new File("nuke.jar");
            ExtensionResources resources = new ExtensionResources(classLoader);

            LoadedExtension<dev.rono.igniscore.api.model.BlockDefinition> extension =
                    new LoadedExtension<>(manifest, jarFile, classLoader, definition, resources);

            assertSame(manifest, extension.getManifest());
            assertSame(jarFile, extension.getJarFile());
            assertSame(classLoader, extension.getClassLoader());
            assertSame(definition, extension.getDefinition());
            assertSame(resources, extension.getResources());
            assertEquals("nuke", extension.getDefinition().getId());
        }
    }
}
