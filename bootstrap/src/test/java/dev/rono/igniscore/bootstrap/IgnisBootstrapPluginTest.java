package dev.rono.igniscore.bootstrap;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisBootstrapPluginTest {

    @Test
    void pluginYamlPointsAtBootstrapEntrypoint() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/plugin.yml")) {
            assertNotNull(input, "plugin.yml should be on the test classpath");
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(yaml.contains("dev.rono.igniscore.bootstrap.IgnisBootstrapPlugin"));
        }
    }

    @Test
    void spongeManifestListsBothSpongeRuntimes() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/META-INF/sponge_plugins.json")) {
            assertNotNull(input, "sponge_plugins.json should be bundled");
            String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("igniscore"));
            assertTrue(json.contains("igniscore-v850"));
        }
    }

    @Test
    void shadedJarRegistersPaperBootloader() throws Exception {
        java.nio.file.Path jar = java.nio.file.Path.of("target/igniscore-1.0.0.jar");
        org.junit.jupiter.api.Assumptions.assumeTrue(java.nio.file.Files.isRegularFile(jar),
                "Packaged bootstrap JAR is required for this check");

        try (java.util.jar.JarFile jarFile = new java.util.jar.JarFile(jar.toFile())) {
            java.util.jar.JarEntry entry = jarFile.getJarEntry(
                    "META-INF/services/dev.rono.igniscore.api.port.PlatformBootloader");
            assertNotNull(entry, "PlatformBootloader service file should be shaded into the JAR");
            String services = new String(jarFile.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(services.contains("dev.rono.igniscore.paper.boot.PaperV121Bootloader"));
        }
    }
}
