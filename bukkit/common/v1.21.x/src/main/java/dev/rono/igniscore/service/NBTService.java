package dev.rono.igniscore.service;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public class NBTService implements IgnisNbtService {

    @Override
    public void setItemString(IgnisItem item, String key, String value) {
        editItem(BukkitBridge.unwrap(item), nbt -> nbt.setString(key, value));
    }

    @Override
    public String getItemString(IgnisItem item, String key) {
        return readItem(BukkitBridge.unwrap(item), nbt -> nbt.getString(key));
    }

    @Override
    public void setItemInt(IgnisItem item, String key, int value) {
        editItem(BukkitBridge.unwrap(item), nbt -> nbt.setInteger(key, value));
    }

    @Override
    public int getItemInt(IgnisItem item, String key, int defaultValue) {
        Integer value = readItem(BukkitBridge.unwrap(item), nbt -> nbt.getInteger(key));
        return value != null ? value : defaultValue;
    }

    @Override
    public void setItemBoolean(IgnisItem item, String key, boolean value) {
        editItem(BukkitBridge.unwrap(item), nbt -> nbt.setBoolean(key, value));
    }

    @Override
    public boolean getItemBoolean(IgnisItem item, String key, boolean defaultValue) {
        Boolean value = readItem(BukkitBridge.unwrap(item), nbt -> nbt.getBoolean(key));
        return value != null ? value : defaultValue;
    }

    @Override
    public void setEntityString(Object nativeEntity, String key, String value) {
        if (nativeEntity instanceof Entity entity) {
            editEntity(entity, nbt -> nbt.setString(key, value));
        }
    }

    @Override
    public String getEntityString(Object nativeEntity, String key) {
        if (nativeEntity instanceof Entity entity) {
            return readEntity(entity, nbt -> nbt.getString(key));
        }
        return null;
    }

    public void editItem(ItemStack item, Consumer<ReadWriteItemNBT> action) {
        if (item == null || item.getType().isAir()) {
            return;
        }
        NBT.modify(item, action);
    }

    public <T> T readItem(ItemStack item, Function<ReadableItemNBT, T> action) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        return NBT.get(item, action);
    }

    public void editEntity(Entity entity, Consumer<ReadWriteNBT> action) {
        if (entity == null) {
            return;
        }
        NBT.modify(entity, action);
    }

    public <T> T readEntity(Entity entity, Function<ReadableNBT, T> action) {
        if (entity == null) {
            return null;
        }
        return NBT.get(entity, action);
    }

    public ReadWriteNBT createCompound() {
        return NBT.createNBTObject();
    }
}
