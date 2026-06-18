package dev.rono.igniscore.paper;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IgnisPaperPluginTest {

    @Test
    void paperPluginYamlPointsAtPaperEntrypoint() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/paper-plugin.yml")) {
            assertNotNull(input, "paper-plugin.yml should be on the test classpath");
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(yaml.contains("dev.rono.igniscore.paper.IgnisPaperPlugin"));
        }
    }

    @Test
    void defaultConfigResourceIsConfigYaml() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/config.yml")) {
            assertNotNull(input, "config.yml should be on the test classpath");
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(yaml.contains("resource-pack:"));
        }
    }

    @Test
    void paperPluginYamlDoesNotDeclareLegacyCommands() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/paper-plugin.yml")) {
            assertNotNull(input, "paper-plugin.yml should be on the test classpath");
            String yaml = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            assertFalse(yaml.contains("\ncommands:"), "Paper command registration is programmatic");
        }
    }
}
