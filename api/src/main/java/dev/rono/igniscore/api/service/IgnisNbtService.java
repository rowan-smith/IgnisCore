package dev.rono.igniscore.api.service;

import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IgnisNbtService {
    void editItem(ItemStack item, Consumer<ReadWriteItemNBT> action);

    <T> T readItem(ItemStack item, Function<ReadableItemNBT, T> action);

    void editEntity(Entity entity, Consumer<ReadWriteNBT> action);

    <T> T readEntity(Entity entity, Function<ReadableNBT, T> action);
}
