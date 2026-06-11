package dev.rono.igniscore.api.extension;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public abstract class ExtensionManifest {
    private final String id;
    private final String name;
    private final String version;
    private final String apiVersion;
    private final String strategyClass;
    private final String author;

    protected ExtensionManifest(String id, String name, String version, String apiVersion, String strategyClass, String author) {
        this.id = Objects.requireNonNull(id, "id");
        this.name = name != null ? name : id;
        this.version = version != null ? version : "1.0.0";
        this.apiVersion = apiVersion != null ? apiVersion : "1.0.0";
        this.strategyClass = Objects.requireNonNull(strategyClass, "strategy");
        this.author = author != null ? author : "unknown";
    }

    protected static YamlConfiguration readYaml(InputStream inputStream) {
        return YamlConfiguration.loadConfiguration(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    protected static String requireStrategyClass(YamlConfiguration config) {
        String strategy = config.getString("strategy");
        if (strategy != null && !strategy.isBlank()) {
            return strategy;
        }

        // Legacy support: main pointed at a BlockPlugin that wraps a Strategy class
        String main = config.getString("main");
        if (main != null && main.endsWith("BlockPlugin")) {
            return main.replace("BlockPlugin", "Strategy");
        }

        return Objects.requireNonNull(strategy, "extension manifest requires strategy");
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

    /** @deprecated use {@link #getStrategyClass()} */
    @Deprecated
    public String getMainClass() {
        return strategyClass;
    }

    public String getAuthor() {
        return author;
    }
}
