package dev.rono.igniscore.api.config;

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.IgnisStrategyDescriptor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses YAML block and item definitions without a platform configuration API.
 */
public final class YamlDefinitions {
    private static final Yaml YAML = new Yaml();
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private YamlDefinitions() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadMap(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            Object loaded = YAML.load(reader);
            if (loaded instanceof Map<?, ?> map) {
                return (Map<String, Object>) map;
            }
            return Map.of();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load YAML from " + path, e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadMap(InputStream stream) {
        Object loaded = YAML.load(stream);
        if (loaded instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    public static BlockDefinition parseBlock(Map<String, Object> config, String fallbackId, int modelData, String extensionId) {
        return DefinitionParser.parseBlock(config, fallbackId, modelData, extensionId);
    }

    public static ItemDefinition parseItem(Map<String, Object> config, String fallbackId, int modelData, String extensionId) {
        return DefinitionParser.parseItem(config, fallbackId, modelData, extensionId);
    }

    public static ExtensionManifest parseManifest(Map<String, Object> config, Path source) {
        return ExtensionManifest.fromMap(config, source);
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> section(Map<String, Object> root, String key) {
        Object value = root.get(key);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    @SuppressWarnings("unchecked")
    static List<String> stringList(Map<String, Object> root, String key) {
        Object value = root.get(key);
        if (value instanceof List<?> list) {
            List<String> result = new ArrayList<>();
            for (Object entry : list) {
                if (entry != null) {
                    result.add(entry.toString());
                }
            }
            return result;
        }
        return List.of();
    }

    public static String string(Map<String, Object> root, String key, String defaultValue) {
        Object value = root.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    static boolean bool(Map<String, Object> root, String key, boolean defaultValue) {
        Object value = root.get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        return defaultValue;
    }

    static int integer(Map<String, Object> root, String key, int defaultValue) {
        Object value = root.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    static double decimal(Map<String, Object> root, String key, double defaultValue) {
        Object value = root.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    static Component component(String raw) {
        return LEGACY.deserialize(raw);
    }

    static Map<String, Object> flattenSection(Map<String, Object> section) {
        return new HashMap<>(section);
    }
}
