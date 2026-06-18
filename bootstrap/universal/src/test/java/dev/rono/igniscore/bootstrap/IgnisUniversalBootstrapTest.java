package dev.rono.igniscore.bootstrap;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisUniversalBootstrapTest {

    @Test
    void pluginYamlPointsAtSpigotEntrypoint() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/plugin.yml")) {
            assertNotNull(input, "plugin.yml should be on the test classpath");
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(yaml.contains("dev.rono.igniscore.spigot.IgnisCorePlugin"));
        }
    }

    @Test
    void paperPluginYamlPointsAtPaperEntrypoint() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/paper-plugin.yml")) {
            assertNotNull(input, "paper-plugin.yml should be on the test classpath");
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(yaml.contains("dev.rono.igniscore.paper.IgnisPaperPlugin"));
        }
    }

    @Test
    void spongeManifestListsAllSpongeRuntimes() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/META-INF/sponge_plugins.json")) {
            assertNotNull(input, "sponge_plugins.json should be bundled");
            String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(json.contains("igniscore"));
            assertTrue(json.contains("igniscore-v850"));
            assertTrue(json.contains("igniscore-v1900"));
        }
    }
}
