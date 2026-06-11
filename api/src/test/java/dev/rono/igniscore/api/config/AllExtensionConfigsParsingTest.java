package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.IgnisApiVersion;
import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AllExtensionConfigsParsingTest {
    @ParameterizedTest
    @MethodSource("blockConfigs")
    void parsesEveryBlockExtensionConfig(Path configPath) throws IOException {
        YamlConfiguration config = load(configPath);
        String extensionId = configPath.getName(configPath.getNameCount() - 5).toString();
        ExtensionManifest manifest = manifestFor(extensionId, "block-extension.yml");

        BlockDefinition definition = DefinitionParser.parseBlock(config, extensionId, 10001, extensionId);
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(config, manifest);

        assertFalse(definition.getId().isBlank());
        assertFalse(descriptor.getId().isBlank());
        assertNotNull(definition.getStrategy());
        assertEqualsExtension(extensionId, definition.getExtensionId());
    }

    @ParameterizedTest
    @MethodSource("itemConfigs")
    void parsesEveryItemExtensionConfig(Path configPath) throws IOException {
        YamlConfiguration config = load(configPath);
        String extensionId = configPath.getName(configPath.getNameCount() - 5).toString();
        ExtensionManifest manifest = manifestFor(extensionId, "item-extension.yml");

        ItemDefinition definition = DefinitionParser.parseItem(config, extensionId, 20001, extensionId);
        IgnisStrategyDescriptor descriptor = DefinitionParser.parseStrategyDescriptor(config, manifest);

        assertFalse(definition.getId().isBlank());
        assertFalse(descriptor.getId().isBlank());
        assertNotNull(definition.getStrategy());
        assertEqualsExtension(extensionId, definition.getExtensionId());
    }

    private static void assertEqualsExtension(String expected, String actual) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, actual);
    }

    private static Stream<Path> blockConfigs() throws IOException {
        return extensionConfigs("blocks");
    }

    private static Stream<Path> itemConfigs() throws IOException {
        return extensionConfigs("items");
    }

    private static Stream<Path> extensionConfigs(String category) throws IOException {
        Path root = Path.of("..", category);
        if (!Files.isDirectory(root)) {
            return Stream.empty();
        }

        try (Stream<Path> modules = Files.list(root)) {
            return modules
                    .map(module -> module.resolve("src/main/resources/config.yml"))
                    .filter(Files::exists)
                    .toList()
                    .stream();
        }
    }

    private static YamlConfiguration load(Path configPath) throws IOException {
        return YamlConfiguration.loadConfiguration(new java.io.StringReader(
                Files.readString(configPath, StandardCharsets.UTF_8)));
    }

    private static ExtensionManifest manifestFor(String extensionId, String fileName) throws IOException {
        String category = fileName.startsWith("block") ? "blocks" : "items";
        Path manifestPath = Path.of("..", category, extensionId, "src/main/resources", fileName);
        String manifest = Files.readString(manifestPath, StandardCharsets.UTF_8)
                .replace("@project.version@", IgnisApiVersion.CURRENT);
        return ExtensionManifest.fromStream(
                new java.io.ByteArrayInputStream(manifest.getBytes(StandardCharsets.UTF_8)),
                fileName);
    }
}
