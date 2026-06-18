package dev.rono.igniscore.bootstrap;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.jar.JarFile;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisUniversalBootstrapJarIT {

    @Test
    void shadedJarContainsAllPlatformEntrypoints() throws Exception {
        Path jar = Path.of("target/IgnisCore-1.0.0.jar");

        try (JarFile jarFile = new JarFile(jar.toFile())) {
            var serviceEntry = jarFile.getJarEntry(
                    "META-INF/services/dev.rono.igniscore.api.port.PlatformBootloader");
            assertNotNull(serviceEntry, "PlatformBootloader service file should be shaded into the JAR");

            String services = new String(jarFile.getInputStream(serviceEntry).readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(services.contains("dev.rono.igniscore.spigot.boot.SpigotV121Bootloader"));
            assertTrue(services.contains("dev.rono.igniscore.paper.boot.PaperV121Bootloader"));
            assertTrue(services.contains("dev.rono.igniscore.sponge.boot.SpongeV1200Bootloader"));

            assertNotNull(jarFile.getEntry("dev/rono/igniscore/spigot/IgnisCorePlugin.class"));
            assertNotNull(jarFile.getEntry("dev/rono/igniscore/paper/IgnisPaperPlugin.class"));
            assertNotNull(jarFile.getEntry("plugin.yml"));
            assertNotNull(jarFile.getEntry("paper-plugin.yml"));
            assertNotNull(jarFile.getEntry("META-INF/sponge_plugins.json"));
        }
    }
}
