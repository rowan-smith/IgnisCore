package dev.rono.igniscore.bukkit;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisCorePluginTest {

    @Test
    void pluginYamlPointsAtBukkitEntrypoint() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/plugin.yml")) {
            assertNotNull(input, "plugin.yml should be on the test classpath");
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(yaml.contains("dev.rono.igniscore.bukkit.IgnisCorePlugin"));
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
    void spongeManifestListsBothSpongeRuntimes() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/META-INF/sponge_plugins.json")) {
            assertNotNull(input, "sponge_plugins.json should be bundled");
            String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("igniscore"));
            assertTrue(json.contains("igniscore-v850"));
        }
    }

    @Test
    void bukkitJarIncludesAdventurePlatformDependency() throws Exception {
        assertNotNull(Class.forName("net.kyori.adventure.platform.bukkit.BukkitAudiences"));
    }
}
