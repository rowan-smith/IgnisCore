package dev.rono.igniscore.sponge.v1900.adapter;

import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;

public final class SpongeIgnisInventory implements IgnisInventory {
    private final Inventory handle;

    public SpongeIgnisInventory(Inventory handle) {
        this.handle = handle;
    }

    public Inventory getHandle() {
        return handle;
    }

    @Override
    public int getSize() {
        return handle.capacity();
    }

    @Override
    public void setItem(int slot, IgnisItem item) {
        ItemStack stack = SpongeBridge.unwrap(item);
        if (stack == null || stack.isEmpty()) {
            handle.set(slot, ItemStack.empty());
        } else {
            handle.set(slot, stack);
        }
    }

    @Override
    public IgnisItem getItem(int slot) {
        return handle.peekAt(slot)
                .map(SpongeBridge::wrap)
                .orElse(null);
    }

    @Override
    public Object nativeInventory() {
        return handle;
    }
}
