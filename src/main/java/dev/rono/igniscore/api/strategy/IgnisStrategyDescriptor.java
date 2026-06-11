package dev.rono.igniscore.api.strategy;

import java.util.Objects;

public final class IgnisStrategyDescriptor {
    private final String id;
    private final String name;
    private final String version;
    private final String author;
    private final String sourcePlugin;

    public IgnisStrategyDescriptor(String id, String name, String version, String author, String sourcePlugin) {
        this.id = Objects.requireNonNull(id, "id").toLowerCase();
        this.name = name != null ? name : id;
        this.version = version != null ? version : "1.0.0";
        this.author = author != null ? author : "unknown";
        this.sourcePlugin = sourcePlugin != null ? sourcePlugin : "builtin";
    }

    public static IgnisStrategyDescriptor of(String id, String name, String version, String author) {
        return new IgnisStrategyDescriptor(id, name, version, author, "builtin");
    }

    public static IgnisStrategyDescriptor of(String id, String name, String version, String author, String sourcePlugin) {
        return new IgnisStrategyDescriptor(id, name, version, author, sourcePlugin);
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

    public String getSourcePlugin() {
        return sourcePlugin;
    }
}
