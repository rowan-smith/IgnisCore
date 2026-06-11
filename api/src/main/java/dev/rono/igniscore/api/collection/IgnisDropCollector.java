package dev.rono.igniscore.api.collection;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

/**
 * Collects item drops from a nearby block break or item spawn.
 * Return true when any items were absorbed.
 */
@FunctionalInterface
public interface IgnisDropCollector {

    boolean tryCollect(Location location, Collection<ItemStack> drops);
}
