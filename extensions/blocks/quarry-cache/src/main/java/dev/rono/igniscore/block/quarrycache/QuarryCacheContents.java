package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.util.Locations;

import java.util.HashMap;
import java.util.Map;

record QuarryCacheContents(Map<Integer, StoredItem> slots) {
    QuarryCacheContents {
        slots = Map.copyOf(slots);
    }

    static QuarryCacheContents empty() {
        return new QuarryCacheContents(Map.of());
    }

    boolean isEmpty() {
        return slots.isEmpty();
    }

    Map<Integer, StoredItem> copySlots() {
        return new HashMap<>(slots);
    }

    record StoredItem(String materialKey, int amount) {
        static StoredItem from(IgnisItem item) {
            if (item == null || item.isAir()) {
                return null;
            }
            return new StoredItem(item.getMaterialKey(), item.getAmount());
        }

        IgnisItem toItem(QuarryCacheInventory inventory) {
            return inventory.createItem(materialKey, amount);
        }
    }
}
