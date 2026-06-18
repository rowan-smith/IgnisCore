package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisItem;
import org.bukkit.inventory.ItemStack;

public final class BukkitIgnisItem implements IgnisItem {
    private final ItemStack handle;

    public BukkitIgnisItem(ItemStack handle) {
        this.handle = handle;
    }

    public ItemStack getHandle() {
        return handle;
    }

    @Override
    public int getAmount() {
        return handle.getAmount();
    }

    @Override
    public void setAmount(int amount) {
        handle.setAmount(amount);
    }

    @Override
    public String getMaterialKey() {
        return handle.getType().name().toLowerCase(java.util.Locale.ROOT);
    }

    @Override
    public boolean isAir() {
        return handle.getType().isAir();
    }

    @Override
    public Object nativeItem() {
        return handle;
    }
}
