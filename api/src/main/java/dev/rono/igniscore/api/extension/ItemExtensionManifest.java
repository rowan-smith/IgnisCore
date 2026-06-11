package dev.rono.igniscore.api.extension;

import java.io.InputStream;
import java.util.Objects;

public final class ItemExtensionManifest extends ExtensionManifest {
    private ItemExtensionManifest(String id, String name, String version, String apiVersion, String strategyClass, String author) {
        super(id, name, version, apiVersion, strategyClass, author);
    }

    public static ItemExtensionManifest fromStream(InputStream inputStream) {
        var config = readYaml(inputStream);
        String id = Objects.requireNonNull(config.getString("id"), "item-extension.yml requires id");
        return new ItemExtensionManifest(
                id,
                config.getString("name", id),
                config.getString("version", "1.0.0"),
                config.getString("api-version", "1.0.0"),
                requireStrategyClass(config),
                config.getString("author", "unknown")
        );
    }
}
