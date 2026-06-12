package dev.rono.blocks.quarrycache;

import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import dev.rono.igniscore.api.strategy.StrategySupport;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.function.Consumer;

final class QuarryCacheInventory implements IgnisCustomInventory {
    static final int FILTER_SLOTS = 5;
    static final int SEPARATOR_START = 5;
    static final int STORAGE_START = 9;
    static final int STORAGE_SLOTS = 45;
    static final int TOTAL_SLOTS = 54;

    private final Location location;
    private final Inventory inventory;
    private Consumer<QuarryCacheInventory> onChanged = ignored -> {};

    QuarryCacheInventory(Location location, Component title) {
        this.location = location.clone();
        this.inventory = StrategySupport.createInventory(null, TOTAL_SLOTS, title);
        fillSeparators();
    }

    void setOnChanged(Consumer<QuarryCacheInventory> onChanged) {
        this.onChanged = onChanged == null ? ignored -> { } : onChanged;
    }

    static boolean isFilterSlot(int slot) {
        return slot >= 0 && slot < FILTER_SLOTS;
    }

    @Override
    public boolean isSeparatorSlot(int slot) {
        return slot >= SEPARATOR_START && slot < STORAGE_START;
    }

    static boolean isStorageSlot(int slot) {
        return slot >= STORAGE_START && slot < TOTAL_SLOTS;
    }

    private void fillSeparators() {
        var separator = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        var meta = separator.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(" ");
            separator.setItemMeta(meta);
        }

        for (int slot = SEPARATOR_START; slot < STORAGE_START; slot++) {
            inventory.setItem(slot, separator.clone());
        }
    }

    Location getCacheLocation() {
        return location.clone();
    }

    ItemStack[] getFilterItems() {
        var filters = new ItemStack[FILTER_SLOTS];

        for (int i = 0; i < FILTER_SLOTS; i++) {
            ItemStack item = inventory.getItem(i);
            filters[i] = item == null || item.getType().isAir() ? null : item.clone();
        }

        return filters;
    }

    @Override
    public void restoreDecorations() {
        fillSeparators();
    }

    @Override
    public @NonNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }

        var filters = getFilterItems();
        boolean hasFilter = Arrays.stream(filters).anyMatch(item -> item != null && !item.getType().isAir());

        if (!hasFilter) {
            return true;
        }

        for (var filter : filters) {
            if (filter != null && !filter.getType().isAir() && filter.getType() == stack.getType()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onChange() {
        notifyChanged();
    }

    @Override
    public void onClose() {
        onChanged.accept(this);
    }

    void notifyChanged() {
        onChanged.accept(this);
    }
}
