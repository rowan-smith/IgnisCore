package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.service.IgnisNbtService;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class BlockItemIdentifier {
    private final IgnisNbtService nbtService;
    private final NamespacedKey blockTypeKey;
    private final NamespacedKey legacyTntTypeKey;

    @Inject
    public BlockItemIdentifier(Plugin plugin, IgnisNbtService nbtService) {
        this.nbtService = nbtService;
        this.blockTypeKey = new NamespacedKey(plugin, "block_type");
        this.legacyTntTypeKey = new NamespacedKey("igniscore", "tnt_type");
    }

    public String resolveTypeId(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }

        String typeId = nbtService.readItem(item, nbt -> nbt.getString("ignis:block_id"));
        if (typeId != null && !typeId.isEmpty()) {
            return typeId;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        typeId = meta.getPersistentDataContainer().get(blockTypeKey, PersistentDataType.STRING);
        if (typeId != null) {
            return typeId;
        }
        return meta.getPersistentDataContainer().get(legacyTntTypeKey, PersistentDataType.STRING);
    }
}
