package dev.rono.igniscore.bootstrap;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisBootstrapJarIT {

    @Test
    void shadedJarRegistersBootloadersAndAdventurePlatform() throws Exception {
        Path jar = Path.of("target/igniscore-1.0.0.jar");

        try (JarFile jarFile = new JarFile(jar.toFile())) {
            var serviceEntry = jarFile.getJarEntry(
                    "META-INF/services/dev.rono.igniscore.api.port.PlatformBootloader");
            assertNotNull(serviceEntry, "PlatformBootloader service file should be shaded into the JAR");

            String services = new String(jarFile.getInputStream(serviceEntry).readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(services.contains("dev.rono.igniscore.paper.boot.PaperV121Bootloader"));
            assertNotNull(jarFile.getEntry("net/kyori/adventure/platform/bukkit/BukkitAudiences.class"),
                    "BukkitAudiences should be bundled for Spigot runtimes");
        }
    }
}
