package dev.rono.igniscore.loader;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class StrategyPluginManifest {
    private final String id;
    private final String name;
    private final String version;
    private final String apiVersion;
    private final String mainClass;
    private final String author;

    private StrategyPluginManifest(String id, String name, String version, String apiVersion, String mainClass, String author) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.apiVersion = apiVersion;
        this.mainClass = mainClass;
        this.author = author;
    }

    public static StrategyPluginManifest fromStream(InputStream inputStream) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String id = Objects.requireNonNull(config.getString("id"), "strategy-plugin.yml requires id");
        String mainClass = Objects.requireNonNull(config.getString("main"), "strategy-plugin.yml requires main");
        return new StrategyPluginManifest(
                id,
                config.getString("name", id),
                config.getString("version", "1.0.0"),
                config.getString("api-version", "1.0.0"),
                mainClass,
                config.getString("author", "unknown")
        );
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

    public String getMainClass() {
        return mainClass;
    }

    public String getAuthor() {
        return author;
    }
}
