package dev.rono.igniscore.api.model;

import dev.rono.igniscore.api.port.IgnisLocation;

import java.util.Objects;

/**
 * A custom block placed in the world: its type definition plus world position.
 *
 * <p>This is the object returned by {@link dev.rono.igniscore.api.event.BlockEvent#block()}.
 * Use {@link #definition()} for extension config/behavior and {@link #location()} for
 * world coordinates.</p>
 */
public final class PlacedBlock {
    private final BlockDefinition definition;
    private final IgnisLocation location;

    public PlacedBlock(BlockDefinition definition, IgnisLocation location) {
        this.definition = Objects.requireNonNull(definition, "definition");
        this.location = Objects.requireNonNull(location, "location");
    }

    public static PlacedBlock of(BlockDefinition definition, IgnisLocation location) {
        return new PlacedBlock(definition, location);
    }

    public static PlacedBlock from(RuntimeBlockInstance instance) {
        return new PlacedBlock(instance.getDefinition(), instance.getLocation());
    }

    /**
     * @return the extension block type loaded from config.yml
     */
    public BlockDefinition definition() {
        return definition;
    }

    /**
     * @return the world position of this placed block
     */
    public IgnisLocation location() {
        return location;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof PlacedBlock placed
                && definition.equals(placed.definition)
                && location.equals(placed.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(definition, location);
    }
}
