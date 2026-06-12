package dev.rono.igniscore.item.detonator;

import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import dev.rono.igniscore.api.service.IgnisNbtService;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

final class DetonatorLinkStorage {
    static final String LINKED_BOMBS_KEY = "ignis:linked_bombs";

    private final IgnisNbtService nbtService;

    DetonatorLinkStorage(IgnisNbtService nbtService) {
        this.nbtService = nbtService;
    }

    List<String> readLinkedBombs(ItemStack item) {
        List<String> linked = nbtService.readItem(item, this::readLinkedBombs);
        return linked == null ? new ArrayList<>() : new ArrayList<>(linked);
    }

    void writeLinkedBombs(ItemStack item, List<String> linkedBombs) {
        nbtService.editItem(item, nbt -> writeLinkedBombs(nbt, linkedBombs));
    }

    private List<String> readLinkedBombs(ReadableItemNBT nbt) {
        if (nbt == null || !nbt.hasNBTData()) {
            return new ArrayList<>();
        }

        try {
            List<String> values = nbt.getStringList(LINKED_BOMBS_KEY).toListCopy();
            return values == null ? new ArrayList<>() : new ArrayList<>(values);
        } catch (Exception ignored) {
            String legacy = nbt.getString(LINKED_BOMBS_KEY);
            if (legacy == null || legacy.isBlank()) {
                return new ArrayList<>();
            }
            return new ArrayList<>(List.of(legacy.split("\\|")));
        }
    }

    private void writeLinkedBombs(ReadWriteItemNBT nbt, List<String> linkedBombs) {
        if (linkedBombs.isEmpty()) {
            nbt.removeKey(LINKED_BOMBS_KEY);
            return;
        }

        var list = nbt.getStringList(LINKED_BOMBS_KEY);
        list.clear();
        list.addAll(linkedBombs);
    }
}
