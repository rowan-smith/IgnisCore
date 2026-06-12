package dev.rono.igniscore.block.quarrycache;

import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

record QuarryCacheContents(Map<Integer, ItemStack> slots) {
    QuarryCacheContents(Map<Integer, ItemStack> slots) {
        this.slots = slots == null ? Map.of() : Map.copyOf(slots);
    }

    static QuarryCacheContents empty() {
        return new QuarryCacheContents(Map.of());
    }

    @Override
    public Map<Integer, ItemStack> slots() {
        return Collections.unmodifiableMap(slots);
    }

    boolean isEmpty() {
        return slots.isEmpty();
    }

    Map<Integer, ItemStack> copySlots() {
        var copy = new HashMap<Integer, ItemStack>();

        for (var entry : slots.entrySet()) {
            copy.put(entry.getKey(), entry.getValue().clone());
        }

        return copy;
    }
}
