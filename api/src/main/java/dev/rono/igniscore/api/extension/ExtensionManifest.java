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

/**
 * Parsed metadata from {@code block-extension.yml} or {@code item-extension.yml}
 * inside an extension JAR.
 *
 * <p>The manifest {@link #getId() id} is the strategy registry key. It should match
 * the {@code id} in {@code config.yml}, which becomes the in-game type id used by
 * commands and NBT.</p>
 *
 * <h2>Optional manifest fields</h2>
 * <ul>
 *   <li>{@code requires-integrations} — {@link ExtensionIntegration} tokens</li>
 *   <li>{@code profiles} — {@link ExtensionProfile} hints for authors and tooling</li>
 * </ul>
 *
 * @see ExtensionRequirements
 * @see ExtensionResources
 */
public final class ExtensionManifest {
    private final String id;
    private final String name;
    private final String version;
    private final String apiVersion;
    private final String strategyClass;
    private final String author;
    private final List<ExtensionIntegration> requiredIntegrations;
    private final List<ExtensionProfile> profiles;

    private ExtensionManifest(String id,
                              String name,
                              String version,
                              String apiVersion,
                              String strategyClass,
                              String author,
                              List<ExtensionIntegration> requiredIntegrations,
                              List<ExtensionProfile> profiles) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = name != null ? name : id;
        this.version = version != null ? version : "1.0.0";
        this.apiVersion = apiVersion != null ? apiVersion : IgnisApiVersion.CURRENT;
        this.strategyClass = Objects.requireNonNull(strategyClass, "strategy");
        this.author = author != null ? author : "unknown";
        this.requiredIntegrations = List.copyOf(requiredIntegrations);
        this.profiles = List.copyOf(profiles);
    }

    /**
     * Parses a manifest YAML stream (typically {@code *-extension.yml}).
     *
     * @param inputStream manifest resource stream
     * @param manifestFileName file name used in error messages
     * @return parsed manifest
     */
    public static ExtensionManifest fromStream(InputStream inputStream, String manifestFileName) {
        return fromMap(YamlDefinitions.loadMap(inputStream), manifestFileName, null);
    }

    /**
     * Parses a manifest from a loaded YAML map.
     *
     * @param config root YAML map
     * @param source optional path for error context
     * @return parsed manifest
     */
    public static ExtensionManifest fromMap(Map<String, Object> config, Path source) {
        String fileName = source != null ? source.getFileName().toString() : "extension.yml";
        return fromMap(config, fileName, source);
    }

    /**
     * Resolves manifest metadata from the extension manifest and config.yml, falling back to the
     * JAR file name when legacy or partially built extension packages omit {@code id} in the manifest.
     *
     * @param manifestConfig parsed {@code *-extension.yml}
     * @param definitionConfig parsed {@code config.yml}
     * @param manifestFileName manifest file name
     * @param jarFallbackId id inferred from the JAR file name when missing
     * @return merged manifest
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
                parseIntegrations(config),
                parseProfiles(config)
        );
    }

    private static List<ExtensionIntegration> parseIntegrations(Map<String, Object> config) {
        List<String> raw = YamlDefinitions.stringList(config, "requires-integrations");
        List<ExtensionIntegration> integrations = new ArrayList<>();
        for (String entry : raw) {
            integrations.add(ExtensionIntegration.fromManifest(entry));
        }
        return integrations;
    }

    private static List<ExtensionProfile> parseProfiles(Map<String, Object> config) {
        List<String> raw = YamlDefinitions.stringList(config, "profiles");
        List<ExtensionProfile> profiles = new ArrayList<>();
        for (String entry : raw) {
            profiles.add(ExtensionProfile.fromManifest(entry));
        }
        return profiles;
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

    /**
     * @return manifest id and strategy registry key
     */
    public String getId() {
        return id;
    }

    /**
     * @return human-readable extension name
     */
    public String getName() {
        return name;
    }

    /**
     * @return extension semver from the manifest
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return declared {@link IgnisApiVersion} compatibility line
     */
    public String getApiVersion() {
        return apiVersion;
    }

    /**
     * @return fully qualified strategy class name
     */
    public String getStrategyClass() {
        return strategyClass;
    }

    /**
     * @return manifest author field
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @return integrations that must be present (may warn or fail at load time)
     */
    public List<ExtensionIntegration> getRequiredIntegrations() {
        return requiredIntegrations;
    }

    /**
     * @return declared behavior profiles (documentation and validation hints)
     */
    public List<ExtensionProfile> getProfiles() {
        return profiles;
    }
}
