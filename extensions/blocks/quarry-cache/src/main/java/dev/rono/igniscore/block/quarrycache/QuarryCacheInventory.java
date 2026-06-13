package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.function.Consumer;

final class QuarryCacheInventory implements IgnisCustomInventory {
    static final int FILTER_START = 2;
    static final int FILTER_SLOTS = 5;
    static final int STORAGE_START = 9;
    static final int STORAGE_SLOTS = 45;
    static final int TOTAL_SLOTS = 54;

    private static final int[] DECORATOR_SLOTS = {0, 1, 7, 8};

    private final IgnisLocation location;
    private final IgnisInventory inventory;
    private final ExtensionSupport extensionSupport;
    private Consumer<QuarryCacheInventory> onChanged = ignored -> {};

    QuarryCacheInventory(IgnisLocation location, Component title, ExtensionSupport extensionSupport) {
        this.location = Locations.toBlock(location);
        this.extensionSupport = extensionSupport;
        this.inventory = extensionSupport.createInventory(null, TOTAL_SLOTS, title);
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
        setDecorator(0);
        setDecorator(1);
        setDecorator(7);
        setDecorator(8);
    }

    private void setDecorator(int slot) {
        inventory.setItem(slot, createItem("gray_stained_glass_pane", 1));
    }

    IgnisItem createItem(String materialKey, int amount) {
        return extensionSupport.createItem(materialKey, amount);
    }

    IgnisLocation getCacheLocation() {
        return location;
    }

    IgnisItem[] getFilterItems() {
        var filters = new IgnisItem[FILTER_SLOTS];

        for (int i = 0; i < FILTER_SLOTS; i++) {
            IgnisItem item = inventory.getItem(FILTER_START + i);
            filters[i] = item == null || item.isAir() ? null : item;
        }

        return filters;
    }

    IgnisInventory getInventory() {
        return inventory;
    }

    @Override
    public void restoreDecorations() {
        fillDecorators();
    }

    @Override
    public boolean accepts(IgnisItem stack) {
        if (stack == null || stack.isAir()) {
            return false;
        }

        var filters = getFilterItems();
        boolean hasFilter = Arrays.stream(filters).anyMatch(item -> item != null && !item.isAir());

        if (!hasFilter) {
            return true;
        }

        for (var filter : filters) {
            if (filter != null && !filter.isAir() && filter.getMaterialKey().equals(stack.getMaterialKey())) {
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
