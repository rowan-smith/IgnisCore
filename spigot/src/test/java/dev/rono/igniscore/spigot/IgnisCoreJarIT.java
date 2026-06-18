package dev.rono.igniscore.spigot;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisCoreJarIT {

    @Test
    void shadedJarRegistersSpigotBootloadersAndAdventurePlatform() throws Exception {
        Path jar = Path.of("target/IgnisCore-Spigot-1.0.0.jar");

        try (JarFile jarFile = new JarFile(jar.toFile())) {
            var serviceEntry = jarFile.getJarEntry(
                    "META-INF/services/dev.rono.igniscore.api.port.PlatformBootloader");
            assertNotNull(serviceEntry, "PlatformBootloader service file should be shaded into the JAR");

            String services = new String(jarFile.getInputStream(serviceEntry).readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(services.contains("dev.rono.igniscore.spigot.boot.SpigotV121Bootloader"));
            assertFalseContains(services, "dev.rono.igniscore.paper.boot.PaperV121Bootloader");
            assertNotNull(jarFile.getEntry("net/kyori/adventure/platform/bukkit/BukkitAudiences.class"),
                    "BukkitAudiences should be bundled for Spigot runtimes");
            assertNull(jarFile.getEntry("paper-plugin.yml"),
                    "Paper manifest should not be bundled in the Spigot plugin");
            assertNull(jarFile.getEntry("META-INF/sponge_plugins.json"),
                    "Sponge manifest should not be bundled in the Spigot plugin");
        }
    }

    private static void assertFalseContains(String haystack, String needle) {
        assertTrue(!haystack.contains(needle), "Unexpected entry in bootloader services: " + needle);
    }
}
