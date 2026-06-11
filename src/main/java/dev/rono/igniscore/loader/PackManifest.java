package dev.rono.igniscore.loader;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public final class PackManifest {
    private final String id;
    private final String name;
    private final String version;
    private final String author;
    private final List<String> blocks;
    private final List<String> strategies;

    private PackManifest(String id, String name, String version, String author, List<String> blocks, List<String> strategies) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.author = author;
        this.blocks = blocks;
        this.strategies = strategies;
    }

    public static PackManifest fromFile(File packFolder) {
        File manifestFile = new File(packFolder, "pack.yml");
        if (!manifestFile.exists()) {
            throw new IllegalStateException("Missing pack.yml in " + packFolder.getName());
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(manifestFile);
        String id = config.getString("id", packFolder.getName());
        return new PackManifest(
                id,
                config.getString("name", id),
                config.getString("version", "1.0.0"),
                config.getString("author", "unknown"),
                config.getStringList("blocks"),
                config.getStringList("strategies")
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

    public String getAuthor() {
        return author;
    }

    public List<String> getBlocks() {
        return blocks;
    }

    public List<String> getStrategies() {
        return strategies;
    }
}
