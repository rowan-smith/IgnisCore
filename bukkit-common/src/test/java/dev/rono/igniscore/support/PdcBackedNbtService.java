package dev.rono.igniscore.support;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public final class PdcBackedNbtService implements IgnisNbtService {
    private static final NamespacedKey NAMESPACE = NamespacedKey.fromString("ignis:nbt");

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String providerName() {
        return "pdc";
    }

    @Override
    public boolean supportsEntityData() {
        return false;
    }

    @Override
    public void setItemString(IgnisItem item, String key, String value) {
        setString(BukkitBridge.unwrap(item), key, value);
    }

    @Override
    public String getItemString(IgnisItem item, String key) {
        return getString(BukkitBridge.unwrap(item), key);
    }

    @Override
    public void setItemInt(IgnisItem item, String key, int value) {
        setInteger(BukkitBridge.unwrap(item), key, value);
    }

    @Override
    public int getItemInt(IgnisItem item, String key, int defaultValue) {
        Integer value = getInteger(BukkitBridge.unwrap(item), key);
        return value != null ? value : defaultValue;
    }

    @Override
    public void setItemBoolean(IgnisItem item, String key, boolean value) {
        setBoolean(BukkitBridge.unwrap(item), key, value);
    }

    @Override
    public boolean getItemBoolean(IgnisItem item, String key, boolean defaultValue) {
        Boolean value = getBoolean(BukkitBridge.unwrap(item), key);
        return value != null ? value : defaultValue;
    }

    @Override
    public void setEntityString(Object nativeEntity, String key, String value) {
        throw new UnsupportedOperationException("Entity NBT is not supported in PdcBackedNbtService");
    }

    @Override
    public String getEntityString(Object nativeEntity, String key) {
        throw new UnsupportedOperationException("Entity NBT is not supported in PdcBackedNbtService");
    }

    private static NamespacedKey toKey(String nbtKey) {
        String suffix = nbtKey.startsWith("ignis:") ? nbtKey.substring("ignis:".length()) : nbtKey;
        return new NamespacedKey(NAMESPACE.getNamespace(), suffix);
    }

    private static void setString(ItemStack item, String key, String value) {
        if (item == null || item.getType().isAir()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().set(toKey(key), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    private static void setInteger(ItemStack item, String key, Integer value) {
        if (item == null || item.getType().isAir()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().set(toKey(key), PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }

    private static void setBoolean(ItemStack item, String key, boolean value) {
        if (item == null || item.getType().isAir()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().set(toKey(key), PersistentDataType.BOOLEAN, value);
        item.setItemMeta(meta);
    }

    private static String getString(ItemStack item, String key) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(toKey(key), PersistentDataType.STRING);
    }

    private static Integer getInteger(ItemStack item, String key) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(toKey(key), PersistentDataType.INTEGER);
    }

    private static Boolean getBoolean(ItemStack item, String key) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(toKey(key), PersistentDataType.BOOLEAN);
    }
}
