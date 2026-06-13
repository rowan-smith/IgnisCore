package dev.rono.igniscore.api.collection;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;

import java.util.Collection;

/**
 * Collects item drops from a nearby block break or item spawn.
 * Return true when any items were absorbed.
 */
@FunctionalInterface
public interface IgnisDropCollector {

    boolean tryCollect(IgnisLocation location, Collection<IgnisItem> drops);
}
