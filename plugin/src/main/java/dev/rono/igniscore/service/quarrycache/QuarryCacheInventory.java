package dev.rono.igniscore.service.quarrycache;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public final class QuarryCacheInventory implements InventoryHolder {
    public static final int FILTER_SLOTS = 5;
    public static final int SEPARATOR_START = 5;
    public static final int SEPARATOR_SLOTS = 4;
    public static final int STORAGE_START = 9;
    public static final int STORAGE_SLOTS = 45;
    public static final int TOTAL_SLOTS = 54;

    private final Location location;
    private final Inventory inventory;

    public QuarryCacheInventory(Location location, Component title) {
        this.location = location.clone();
        this.inventory = Bukkit.createInventory(this, TOTAL_SLOTS, title);
        fillSeparators();
    }

    public static boolean isFilterSlot(int slot) {
        return slot >= 0 && slot < FILTER_SLOTS;
    }

    public static boolean isSeparatorSlot(int slot) {
        return slot >= SEPARATOR_START && slot < STORAGE_START;
    }

    public static boolean isStorageSlot(int slot) {
        return slot >= STORAGE_START && slot < TOTAL_SLOTS;
    }

    private void fillSeparators() {
        ItemStack separator = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = separator.getItemMeta();
        if (meta != null) {
            meta.displayName(Component.empty());
            separator.setItemMeta(meta);
        }
        for (int slot = SEPARATOR_START; slot < STORAGE_START; slot++) {
            inventory.setItem(slot, separator.clone());
        }
    }

    public Location getCacheLocation() {
        return location.clone();
    }

    public ItemStack[] getFilterItems() {
        ItemStack[] filters = new ItemStack[FILTER_SLOTS];
        for (int i = 0; i < FILTER_SLOTS; i++) {
            ItemStack item = inventory.getItem(i);
            filters[i] = item == null || item.getType().isAir() ? null : item.clone();
        }
        return filters;
    }

    public void restoreSeparators() {
        fillSeparators();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public boolean accepts(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }

        ItemStack[] filters = getFilterItems();
        boolean hasFilter = Arrays.stream(filters).anyMatch(item -> item != null && !item.getType().isAir());
        if (!hasFilter) {
            return true;
        }

        for (ItemStack filter : filters) {
            if (filter != null && !filter.getType().isAir() && filter.getType() == stack.getType()) {
                return true;
            }
        }
        return false;
    }
}
