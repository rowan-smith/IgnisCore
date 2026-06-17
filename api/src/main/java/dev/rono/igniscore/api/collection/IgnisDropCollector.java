package dev.rono.igniscore.api.collection;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;

import java.util.Collection;

/**
 * Collects item drops spawned near a custom block.
 *
 * <p>Strategies register a collector to absorb drops from adjacent block breaks or item spawns
 * (for example hoppers or processing blocks). Return {@code true} when any items were removed
 * from {@code drops}.</p>
 */
@FunctionalInterface
public interface IgnisDropCollector {

    /**
     * Attempts to absorb drops near {@code location}.
     *
     * @param location block position acting as the collection anchor
     * @param drops mutable collection of spawned items; implementations may remove entries
     * @return {@code true} when at least one drop was collected
     */
    boolean tryCollect(IgnisLocation location, Collection<IgnisItem> drops);
}
