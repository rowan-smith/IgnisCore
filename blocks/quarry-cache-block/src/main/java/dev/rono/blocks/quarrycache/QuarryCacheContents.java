package dev.rono.blocks.quarrycache;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class QuarryCacheContents {
    private final Map<Integer, ItemStack> slots;

    QuarryCacheContents(Map<Integer, ItemStack> slots) {
        this.slots = slots == null ? Map.of() : Map.copyOf(slots);
    }

    static QuarryCacheContents empty() {
        return new QuarryCacheContents(Map.of());
    }

    Map<Integer, ItemStack> getSlots() {
        return Collections.unmodifiableMap(slots);
    }

    boolean isEmpty() {
        return slots.isEmpty();
    }

    Map<Integer, ItemStack> copySlots() {
        Map<Integer, ItemStack> copy = new HashMap<>();
        for (Map.Entry<Integer, ItemStack> entry : slots.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().clone());
        }
        return copy;
    }
}
