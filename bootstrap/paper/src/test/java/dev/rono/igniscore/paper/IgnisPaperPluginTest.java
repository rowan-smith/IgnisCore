package dev.rono.igniscore.paper;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisPaperPluginTest {

    @Test
    void pluginYamlPointsAtPaperEntrypoint() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/plugin.yml")) {
            assertNotNull(input, "plugin.yml should be on the test classpath");
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(yaml.contains("dev.rono.igniscore.paper.IgnisPaperPlugin"));
        }
    }

    @Test
    void defaultConfigResourceIsPaperConfigYaml() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/paper-config.yml")) {
            assertNotNull(input, "paper-config.yml should be on the test classpath");
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(yaml.contains("resource-pack:"));
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
}
