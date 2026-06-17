package dev.rono.igniscore.api.strategy;

import java.util.Objects;

/**
 * Immutable metadata describing a registered behavior strategy.
 *
 * <p>The {@linkplain #getId() id} is normalized to lower case and matches the extension manifest
 * {@code id} used for registry lookups. {@linkplain #getSourcePlugin() source plugin} identifies
 * the JAR or built-in module that supplied the strategy.</p>
 */
public final class IgnisStrategyDescriptor {
    private final String id;
    private final String name;
    private final String version;
    private final String author;
    private final String sourcePlugin;

    /**
     * Creates a descriptor with defaults for optional fields.
     *
     * @param id unique strategy id (normalized to lower case)
     * @param name display name, or {@code null} to use {@code id}
     * @param version strategy version, or {@code null} for {@code 1.0.0}
     * @param author author name, or {@code null} for {@code unknown}
     * @param sourcePlugin owning plugin id, or {@code null} for {@code builtin}
     */
    public IgnisStrategyDescriptor(String id, String name, String version, String author, String sourcePlugin) {
        this.id = Objects.requireNonNull(id, "id").toLowerCase();
        this.name = name != null ? name : id;
        this.version = version != null ? version : "1.0.0";
        this.author = author != null ? author : "unknown";
        this.sourcePlugin = sourcePlugin != null ? sourcePlugin : "builtin";
    }

    /**
     * Creates a built-in strategy descriptor without an explicit source plugin.
     *
     * @param id unique strategy id
     * @param name display name
     * @param version strategy version
     * @param author author name
     * @return descriptor with {@code sourcePlugin} set to {@code builtin}
     */
    public static IgnisStrategyDescriptor of(String id, String name, String version, String author) {
        return new IgnisStrategyDescriptor(id, name, version, author, "builtin");
    }

    /**
     * Creates a strategy descriptor with an explicit source plugin.
     *
     * @param id unique strategy id
     * @param name display name
     * @param version strategy version
     * @param author author name
     * @param sourcePlugin owning plugin or extension id
     * @return new descriptor
     */
    public static IgnisStrategyDescriptor of(String id, String name, String version, String author, String sourcePlugin) {
        return new IgnisStrategyDescriptor(id, name, version, author, sourcePlugin);
    }

    /**
     * Returns the normalized strategy id used for registry lookups.
     *
     * @return lower-case strategy id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the human-readable strategy name.
     *
     * @return display name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the strategy implementation version.
     *
     * @return version string
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the strategy author.
     *
     * @return author name
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the plugin or extension that registered this strategy.
     *
     * @return source plugin id
     */
    public String getSourcePlugin() {
        return sourcePlugin;
    }
}
