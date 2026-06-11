package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.model.ItemDefinition;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.List;

public class ItemFactory {
    private final ItemManager itemManager;
    private final NBTService nbtService;

    @Inject
    public ItemFactory(ItemManager itemManager, NBTService nbtService) {
        this.itemManager = itemManager;
        this.nbtService = nbtService;
    }

    public ItemStack createItem(String typeId) {
        ItemDefinition type = itemManager.getItemTypes().get(typeId);
        if (type == null) {
            throw new IllegalArgumentException("Unknown item type: " + typeId);
        }

        Material material = Material.matchMaterial(type.getBaseMaterial());
        if (material == null) {
            material = Material.PAPER;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(type.getTitle());
            meta.lore(type.getDescription());
            meta.setCustomModelData(type.getCustomModelData());

            CustomModelDataComponent customModelData = meta.getCustomModelDataComponent();
            customModelData.setFloats(List.of((float) type.getCustomModelData()));
            meta.setCustomModelDataComponent(customModelData);
            meta.setItemModel(new NamespacedKey("igniscore", type.getId()));
            item.setItemMeta(meta);
        }

        nbtService.editItem(item, nbt -> {
            nbt.setString("ignis:item_id", typeId);
            nbt.setString("ignis:strategy", type.getStrategy());
            nbt.setInteger("ignis:version", 1);
        });

        return item;
    }
}
