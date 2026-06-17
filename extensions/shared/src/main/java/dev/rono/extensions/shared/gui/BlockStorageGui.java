package dev.rono.extensions.shared.gui;

import dev.rono.igniscore.api.inventory.IgnisCustomInventory;
import dev.rono.igniscore.api.port.IgnisInventory;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.ExtensionSupport;
import dev.rono.igniscore.api.util.Locations;
import net.kyori.adventure.text.Component;

import java.util.function.Consumer;

/**
 * Simple chest-style storage GUI for utility blocks.
 */
public final class BlockStorageGui implements IgnisCustomInventory {
    private final IgnisLocation blockLocation;
    private final IgnisInventory inventory;
    private final ExtensionSupport extensionSupport;
    private final int storageEnd;
    private Consumer<BlockStorageGui> onChanged = ignored -> {};

    public BlockStorageGui(IgnisLocation blockLocation,
                            Component title,
                            ExtensionSupport extensionSupport,
                            int rows) {
        this.blockLocation = Locations.toBlock(blockLocation);
        this.extensionSupport = extensionSupport;
        int size = Math.min(54, Math.max(9, rows * 9));
        this.inventory = extensionSupport.createInventory(null, size, title);
        this.storageEnd = size;
    }

    public void setOnChanged(Consumer<BlockStorageGui> onChanged) {
        this.onChanged = onChanged == null ? ignored -> { } : onChanged;
    }

    public IgnisLocation blockLocation() {
        return blockLocation;
    }

    public IgnisInventory inventory() {
        return inventory;
    }

    public void open(IgnisPlayer player) {
        extensionSupport.openInventory(player, inventory);
    }

    public void register() {
        extensionSupport.registerCustomInventory(inventory.nativeInventory(), this);
    }

    public void unregister() {
        extensionSupport.unregisterCustomInventory(inventory.nativeInventory());
    }

    @Override
    public boolean accepts(IgnisItem stack) {
        return stack != null && !stack.isAir();
    }

    @Override
    public void restoreDecorations() {
    }

    @Override
    public boolean isSeparatorSlot(int slot) {
        return false;
    }

    @Override
    public void onChange() {
        onChanged.accept(this);
    }

    @Override
    public void onClose() {
        onChanged.accept(this);
    }

    public void forEachStored(java.util.function.BiConsumer<Integer, IgnisItem> consumer) {
        for (int slot = 0; slot < storageEnd; slot++) {
            IgnisItem item = inventory.getItem(slot);
            if (item != null && !item.isAir()) {
                consumer.accept(slot, item);
            }
        }
    }
}
