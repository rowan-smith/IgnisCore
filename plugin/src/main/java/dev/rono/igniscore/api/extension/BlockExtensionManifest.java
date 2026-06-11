package dev.rono.igniscore.api.extension;

import java.io.InputStream;
import java.util.Objects;

public final class BlockExtensionManifest extends ExtensionManifest {
    private BlockExtensionManifest(String id, String name, String version, String apiVersion, String mainClass, String author) {
        super(id, name, version, apiVersion, mainClass, author);
    }

    public static BlockExtensionManifest fromStream(InputStream inputStream) {
        var config = readYaml(inputStream);
        String id = Objects.requireNonNull(config.getString("id"), "block-extension.yml requires id");
        String mainClass = Objects.requireNonNull(config.getString("main"), "block-extension.yml requires main");
        return new BlockExtensionManifest(
                id,
                config.getString("name", id),
                config.getString("version", "1.0.0"),
                config.getString("api-version", "1.0.0"),
                mainClass,
                config.getString("author", "unknown")
        );
    }
}
