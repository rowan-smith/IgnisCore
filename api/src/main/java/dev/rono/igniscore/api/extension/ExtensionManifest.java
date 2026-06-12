package dev.rono.igniscore.api.extension;

import dev.rono.igniscore.api.IgnisApiVersion;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ExtensionManifest {
    private final String id;
    private final String name;
    private final String version;
    private final String apiVersion;
    private final String strategyClass;
    private final String author;

    private ExtensionManifest(String id, String name, String version, String apiVersion, String strategyClass, String author) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = name != null ? name : id;
        this.version = version != null ? version : "1.0.0";
        this.apiVersion = apiVersion != null ? apiVersion : IgnisApiVersion.CURRENT;
        this.strategyClass = Objects.requireNonNull(strategyClass, "strategy");
        this.author = author != null ? author : "unknown";
    }

    public static ExtensionManifest fromStream(InputStream inputStream, String manifestFileName) {
        YamlConfiguration config = readYaml(inputStream);
        String id = Objects.requireNonNull(config.getString("id"), manifestFileName + " requires id");
        return new ExtensionManifest(
                id,
                config.getString("name", id),
                config.getString("version", "1.0.0"),
                config.getString("api-version", IgnisApiVersion.CURRENT),
                requireStrategyClass(config, id),
                config.getString("author", "unknown")
        );
    }

    private static YamlConfiguration readYaml(InputStream inputStream) {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    private static String requireStrategyClass(YamlConfiguration config, String extensionId) {
        String strategy = config.getString("strategy");
        if (strategy != null && !strategy.isBlank()) {
            return strategy;
        }

        String main = config.getString("main");
        if (main != null && main.endsWith("BlockPlugin")) {
            return main.replace("BlockPlugin", "Strategy");
        }

        if (extensionId.endsWith("-block")) {
            String segment = extensionId.substring(0, extensionId.length() - "-block".length());
            return "dev.rono.igniscore.block." + toPackageSegment(segment) + ".Strategy";
        }

        if (extensionId.endsWith("-item")) {
            String segment = extensionId.substring(0, extensionId.length() - "-item".length());
            return "dev.rono.igniscore.item." + toPackageSegment(segment) + ".Strategy";
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
}
