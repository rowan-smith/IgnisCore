package dev.rono.igniscore.service;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class NBTService {

    /**
     * Executes an operation on an item's NBT.
     */
    public void editItem(ItemStack item, Consumer<ReadWriteItemNBT> action) {
        if (item == null || item.getType().isAir()) return;
        NBT.modify(item, action);
    }

    /**
     * Reads data from an item's NBT.
     */
    public <T> T readItem(ItemStack item, Function<ReadableItemNBT, T> action) {
        if (item == null || item.getType().isAir()) return null;
        return NBT.get(item, action);
    }

    /**
     * Executes an operation on an entity's NBT.
     */
    public void editEntity(Entity entity, Consumer<ReadWriteNBT> action) {
        if (entity == null) return;
        NBT.modify(entity, action);
    }

    /**
     * Reads data from an entity's NBT.
     */
    public <T> T readEntity(Entity entity, Function<ReadableNBT, T> action) {
        if (entity == null) return null;
        return NBT.get(entity, action);
    }

    /**
     * Creates a new NBT compound that can be used for structured storage.
     */
    public ReadWriteNBT createCompound() {
        return NBT.createNBTObject();
    }
}
