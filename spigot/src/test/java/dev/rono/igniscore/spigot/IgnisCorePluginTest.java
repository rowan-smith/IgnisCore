package dev.rono.igniscore.spigot;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisCorePluginTest {

    @Test
    void pluginYamlPointsAtSpigotEntrypoint() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/plugin.yml")) {
            assertNotNull(input, "plugin.yml should be on the test classpath");
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(yaml.contains("dev.rono.igniscore.spigot.IgnisCorePlugin"));
        }
    }

    @Test
    void pluginYamlDoesNotDeclareLegacyCommands() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/plugin.yml")) {
            assertNotNull(input, "plugin.yml should be on the test classpath");
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertFalse(yaml.contains("\ncommands:"), "Paper command registration is programmatic");
        }
    }

    @Test
    void spigotJarDoesNotBundleSpongeManifest() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/META-INF/sponge_plugins.json")) {
            assertTrue(input == null, "Sponge manifest belongs in sponge and universal bootstrap JARs");
        }
    }

    @Test
    void spigotJarIncludesAdventurePlatformDependency() throws Exception {
        assertNotNull(Class.forName("net.kyori.adventure.platform.bukkit.BukkitAudiences"));
    }
}
