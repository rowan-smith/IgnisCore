package dev.rono.igniscore.block.quarrycache;

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
    static final int FILTER_START = 2;
    static final int FILTER_SLOTS = 5;
    static final int STORAGE_START = 9;
    static final int STORAGE_SLOTS = 45;
    static final int TOTAL_SLOTS = 54;

    private static final int[] DECORATOR_SLOTS = {0, 1, 7, 8};

    private final Location location;
    private final Inventory inventory;
    private Consumer<QuarryCacheInventory> onChanged = ignored -> {};

    QuarryCacheInventory(Location location, Component title) {
        this.location = location.clone();
        this.inventory = StrategySupport.createInventory(null, TOTAL_SLOTS, title);
        fillDecorators();
    }

    void setOnChanged(Consumer<QuarryCacheInventory> onChanged) {
        this.onChanged = onChanged == null ? ignored -> { } : onChanged;
    }

    static boolean isFilterSlot(int slot) {
        return slot >= FILTER_START && slot < FILTER_START + FILTER_SLOTS;
    }

    @Override
    public boolean isSeparatorSlot(int slot) {
        for (int decoratorSlot : DECORATOR_SLOTS) {
            if (slot == decoratorSlot) {
                return true;
            }
        }
        return false;
    }

    static boolean isStorageSlot(int slot) {
        return slot >= STORAGE_START && slot < TOTAL_SLOTS;
    }

    private void fillDecorators() {
        setDecorator(0, "§7Filter Row", "§8- add items to filter");
        setDecorator(1, "§8Place filter items", "§8in center slots →");
        setDecorator(7, "§8← matching items", "§8are collected");
        setDecorator(8, "§7Filter Row", "§8- add items to filter");
    }

    private void setDecorator(int slot, String lineOne, String lineTwo) {
        var pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        var meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(lineOne);
            meta.setLore(java.util.List.of(lineTwo));
            pane.setItemMeta(meta);
        }
        inventory.setItem(slot, pane);
    }

    Location getCacheLocation() {
        return location.clone();
    }

    ItemStack[] getFilterItems() {
        var filters = new ItemStack[FILTER_SLOTS];

        for (int i = 0; i < FILTER_SLOTS; i++) {
            ItemStack item = inventory.getItem(FILTER_START + i);
            filters[i] = item == null || item.getType().isAir() ? null : item.clone();
        }

        return filters;
    }

    @Override
    public void restoreDecorations() {
        fillDecorators();
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
