package dev.rono.igniscore.paper;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisPaperBootstrapJarIT {

    @Test
    void shadedJarRegistersOnlyPaperBootloaders() throws Exception {
        Path jar = Path.of("target/IgnisCore-Paper-1.0.0.jar");

        try (JarFile jarFile = new JarFile(jar.toFile())) {
            var serviceEntry = jarFile.getJarEntry(
                    "META-INF/services/dev.rono.igniscore.api.port.PlatformBootloader");
            assertNotNull(serviceEntry, "PlatformBootloader service file should be shaded into the JAR");

            String services = new String(jarFile.getInputStream(serviceEntry).readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(services.contains("dev.rono.igniscore.paper.boot.PaperV121Bootloader"));
            assertFalseContains(services, "dev.rono.igniscore.spigot.boot.SpigotV121Bootloader");
            assertFalseContains(services, "dev.rono.igniscore.sponge.boot.SpongeV1200Bootloader");

            assertNotNull(jarFile.getEntry("dev/rono/igniscore/paper/IgnisPaperPlugin.class"),
                    "Paper plugin entrypoint should be shaded into the JAR");
            assertNotNull(jarFile.getEntry("paper-plugin.yml"),
                    "paper-plugin.yml should be bundled in the Paper plugin");
            assertNull(jarFile.getEntry("plugin.yml"),
                    "Legacy plugin.yml should not be bundled in the Paper plugin");
            assertNotNull(jarFile.getEntry("config.yml"),
                    "config.yml default template should be bundled in the Paper plugin");
            assertNull(jarFile.getEntry("META-INF/sponge_plugins.json"),
                    "Sponge manifest should not be bundled in the Paper plugin");
        }
    }

    private static void assertFalseContains(String haystack, String needle) {
        assertTrue(!haystack.contains(needle), "Unexpected entry in bootloader services: " + needle);
    }
}
