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
    void bootstrapIncludesAdventurePlatformDependency() throws Exception {
        assertNotNull(Class.forName("net.kyori.adventure.platform.bukkit.BukkitAudiences"));
    }
}
