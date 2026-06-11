package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefinitionParserTest {
    @Test
    void buildsStrategyDescriptorFromConfigAndManifest() {
        ExtensionManifest manifest = ExtensionManifest.fromStream(
                new ByteArrayInputStream("""
                        id: nuclear-block
                        name: Nuclear Block
                        version: 2.0.0
                        author: IgnisCore
                        """.getBytes(StandardCharsets.UTF_8)),
                "block-extension.yml");

        YamlConfiguration config = YamlConfiguration.loadConfiguration(new java.io.StringReader("""
                behavior:
                  strategy: nuclear
                  strategy_name: Nuclear Detonation
                """));

        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(config, manifest);

        assertEquals("nuclear", descriptor.getId());
        assertEquals("Nuclear Detonation", descriptor.getName());
        assertEquals("2.0.0", descriptor.getVersion());
        assertEquals("IgnisCore", descriptor.getAuthor());
        assertEquals("nuclear-block", descriptor.getSourcePlugin());
    }
}
