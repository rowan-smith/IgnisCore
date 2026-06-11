package dev.rono.igniscore.api.extension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ExtensionResourcesTest {
    @TempDir
    Path tempDir;

    @Test
    void opensResourcesFromExtensionClassLoader() throws Exception {
        Path jarPath = tempDir.resolve("extension.jar");
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarPath.toFile()))) {
            jar.putNextEntry(new JarEntry("assets/icon.png"));
            jar.write(new byte[]{1, 2, 3});
            jar.closeEntry();
        }

        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, ClassLoader.getPlatformClassLoader());
        ExtensionResources resources = new ExtensionResources(classLoader);

        try (InputStream stream = resources.open("assets/icon.png")) {
            assertNotNull(stream);
        }

        assertSame(classLoader, resources.getClassLoader());
        assertNull(resources.open("missing-resource.txt"));
    }
}
