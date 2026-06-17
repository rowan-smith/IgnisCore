package dev.rono.igniscore.api.extension;

import dev.rono.igniscore.api.IgnisApiVersion;
import dev.rono.igniscore.api.config.YamlDefinitions;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ExtensionManifest {
    private final String id;
    private final String name;
    private final String version;
    private final String apiVersion;
    private final String strategyClass;
    private final String author;
    private final List<String> requiredIntegrations;

    private ExtensionManifest(String id, String name, String version, String apiVersion,
                              String strategyClass, String author, List<String> requiredIntegrations) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = name != null ? name : id;
        this.version = version != null ? version : "1.0.0";
        this.apiVersion = apiVersion != null ? apiVersion : IgnisApiVersion.CURRENT;
        this.strategyClass = Objects.requireNonNull(strategyClass, "strategy");
        this.author = author != null ? author : "unknown";
        this.requiredIntegrations = List.copyOf(requiredIntegrations != null ? requiredIntegrations : List.of());
    }

    public static ExtensionManifest fromStream(InputStream inputStream, String manifestFileName) {
        return fromMap(YamlDefinitions.loadMap(inputStream), manifestFileName, null);
    }

    public static ExtensionManifest fromMap(Map<String, Object> config, Path source) {
        String fileName = source != null ? source.getFileName().toString() : "extension.yml";
        return fromMap(config, fileName, source);
    }

    /**
     * Resolves manifest metadata from the extension manifest and config.yml, falling back to the
     * JAR file name when legacy or partially built extension packages omit {@code id} in the manifest.
     */
    public static ExtensionManifest fromJarContents(Map<String, Object> manifestConfig,
                                                    Map<String, Object> definitionConfig,
                                                    String manifestFileName,
                                                    String jarFallbackId) {
        Map<String, Object> merged = new HashMap<>();
        if (manifestConfig != null) {
            merged.putAll(manifestConfig);
        }

        String id = YamlDefinitions.string(merged, "id", null);
        if (id == null || id.isBlank()) {
            id = YamlDefinitions.string(definitionConfig, "id", jarFallbackId);
        }
        if (id == null || id.isBlank()) {
            throw new NullPointerException(manifestFileName + " requires id");
        }

        merged.put("id", id);
        return fromMap(merged, manifestFileName, null);
    }

    private static ExtensionManifest fromMap(Map<String, Object> config, String manifestFileName, Path source) {
        String id = Objects.requireNonNull(YamlDefinitions.string(config, "id", null),
                manifestFileName + " requires id");
        return new ExtensionManifest(
                id,
                YamlDefinitions.string(config, "name", id),
                YamlDefinitions.string(config, "version", "1.0.0"),
                YamlDefinitions.string(config, "api-version", IgnisApiVersion.CURRENT),
                requireStrategyClass(config, id, manifestFileName),
                YamlDefinitions.string(config, "author", "unknown"),
                parseRequiredIntegrations(config));
    }

    private static List<String> parseRequiredIntegrations(Map<String, Object> config) {
        Object raw = config.get("requires-integrations");
        if (raw instanceof List<?> list) {
            List<String> integrations = new ArrayList<>();
            for (Object entry : list) {
                if (entry != null) {
                    integrations.add(entry.toString().trim().toLowerCase());
                }
            }
            return integrations;
        }
        return List.of();
    }

    private static String requireStrategyClass(Map<String, Object> config, String extensionId, String manifestFileName) {
        String strategy = YamlDefinitions.string(config, "strategy", null);
        if (strategy != null && !strategy.isBlank()) {
            return strategy;
        }

        String main = YamlDefinitions.string(config, "main", null);
        if (main != null && main.endsWith("BlockPlugin")) {
            return main.replace("BlockPlugin", "Strategy");
        }

        if ("block-extension.yml".equals(manifestFileName)) {
            return "dev.rono.igniscore.block." + toPackageSegment(extensionId) + ".Strategy";
        }

        if ("item-extension.yml".equals(manifestFileName)) {
            return "dev.rono.igniscore.item." + toPackageSegment(extensionId) + ".Strategy";
        }

        return Objects.requireNonNull(strategy, "extension manifest requires strategy");
    }

    private static String toPackageSegment(String name) {
        return name.replace("-", "");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getStrategyClass() {
        return strategyClass;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getRequiredIntegrations() {
        return requiredIntegrations;
    }
}
