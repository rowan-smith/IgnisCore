package dev.rono.igniscore.sponge.v1900.adapter;

import dev.rono.igniscore.api.port.IgnisItem;
import org.spongepowered.api.item.inventory.ItemStack;

public final class SpongeIgnisItem implements IgnisItem {
    private ItemStack handle;

    public SpongeIgnisItem(ItemStack handle) {
        this.handle = handle;
    }

    public ItemStack getHandle() {
        return handle;
    }

    public void setHandle(ItemStack handle) {
        this.handle = handle;
    }

    @Override
    public int getAmount() {
        return handle.quantity();
    }

    @Override
    public void setAmount(int amount) {
        handle.setQuantity(amount);
    }

    @Override
    public String getMaterialKey() {
        return SpongeBridge.materialKey(handle.type());
    }

    @Override
    public boolean isAir() {
        return handle.isEmpty();
    }

    @Override
    public Object nativeItem() {
        return handle;
    }
}
