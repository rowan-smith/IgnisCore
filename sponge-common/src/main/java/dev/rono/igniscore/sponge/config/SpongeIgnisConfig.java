package dev.rono.igniscore.sponge.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.rono.igniscore.config.PerformanceSettings;
import dev.rono.igniscore.sponge.adapter.SpongePlatformAdapter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

@Singleton
public final class SpongeIgnisConfig {
    private static final String DEFAULT_HOST = "0.0.0.0";
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_PUBLIC_URL = "http://0.0.0.0:8080/resourcepack.zip";

    private final Path configFile;
    private Map<String, Object> root = new LinkedHashMap<>();

    @Inject
    public SpongeIgnisConfig(SpongePlatformAdapter platformAdapter) {
        this.configFile = platformAdapter.getDataDirectory().resolve("config.yml");
        load();
    }

    SpongeIgnisConfig(Path configFile) {
        this.configFile = configFile;
        load();
    }

    public void load() {
        try {
            Files.createDirectories(configFile.getParent());
            if (!Files.exists(configFile)) {
                try (InputStream defaults = getClass().getResourceAsStream("/config.yml")) {
                    if (defaults != null) {
                        Files.copy(defaults, configFile);
                    } else {
                        saveDefaults();
                    }
                }
            }
            try (InputStream input = Files.newInputStream(configFile)) {
                Object loaded = new Yaml().load(input);
                root = loaded instanceof Map<?, ?> map ? copyMap(map) : defaults();
            }
        } catch (IOException error) {
            root = defaults();
        }
    }

    public void save() {
        try {
            Files.createDirectories(configFile.getParent());
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            try (Writer writer = Files.newBufferedWriter(configFile)) {
                new Yaml(options).dump(root, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public void reload() {
        load();
    }

    public String resourcePackHost() {
        return string(section("resource-pack"), "host", DEFAULT_HOST);
    }

    public int resourcePackPort() {
        return integer(section("resource-pack"), "port", DEFAULT_PORT);
    }

    public String resourcePackPublicUrl() {
        return string(section("resource-pack"), "public-url", DEFAULT_PUBLIC_URL);
    }

    public PerformanceSettings performanceSettings() {
        Map<String, Object> performance = section("performance");
        return PerformanceSettings.fromValues(
                integer(performance, "chunk-restore-blocks-per-tick",
                        PerformanceSettings.DEFAULT_CHUNK_RESTORE_BLOCKS_PER_TICK),
                integer(performance, "visual-refresh-blocks-per-tick",
                        PerformanceSettings.DEFAULT_VISUAL_REFRESH_BLOCKS_PER_TICK),
                integer(performance, "resource-pack-retain-count",
                        PerformanceSettings.DEFAULT_RESOURCE_PACK_RETAIN_COUNT));
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> section(String key) {
        Object value = root.get(key);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        Map<String, Object> created = new LinkedHashMap<>();
        root.put(key, created);
        return created;
    }

    private static Map<String, Object> defaults() {
        Map<String, Object> config = new LinkedHashMap<>();
        Map<String, Object> resourcePack = new LinkedHashMap<>();
        resourcePack.put("host", DEFAULT_HOST);
        resourcePack.put("port", DEFAULT_PORT);
        resourcePack.put("public-url", DEFAULT_PUBLIC_URL);
        config.put("resource-pack", resourcePack);

        Map<String, Object> performance = new LinkedHashMap<>();
        performance.put("chunk-restore-blocks-per-tick", PerformanceSettings.DEFAULT_CHUNK_RESTORE_BLOCKS_PER_TICK);
        performance.put("visual-refresh-blocks-per-tick", PerformanceSettings.DEFAULT_VISUAL_REFRESH_BLOCKS_PER_TICK);
        performance.put("resource-pack-retain-count", PerformanceSettings.DEFAULT_RESOURCE_PACK_RETAIN_COUNT);
        config.put("performance", performance);
        return config;
    }

    private void saveDefaults() throws IOException {
        root = defaults();
        save();
    }

    private static String string(Map<String, Object> section, String key, String defaultValue) {
        Object value = section.get(key);
        return value == null ? defaultValue : String.valueOf(value);
    }

    private static int integer(Map<String, Object> section, String key, int defaultValue) {
        Object value = section.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value instanceof String text) {
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> copyMap(Map<?, ?> source) {
        Map<String, Object> copy = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();
            if (value instanceof Map<?, ?> nested) {
                copy.put(key, copyMap(nested));
            } else {
                copy.put(key, value);
            }
        }
        return copy;
    }
}
