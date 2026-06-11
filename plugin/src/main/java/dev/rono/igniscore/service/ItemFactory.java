package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.api.model.ItemDefinition;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFactory {
    private final ItemManager itemManager;
    private final IgnisNbtService nbtService;

    @Inject
    public ItemFactory(ItemManager itemManager, IgnisNbtService nbtService) {
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
            meta.setItemModel(new NamespacedKey("igniscore", type.getId()));
            item.setItemMeta(meta);
        }

        item.setData(
                DataComponentTypes.CUSTOM_MODEL_DATA,
                CustomModelData.customModelData().addFloat((float) type.getCustomModelData()).build()
        );

        nbtService.editItem(item, nbt -> {
            nbt.setString("ignis:item_id", typeId);
            nbt.setString("ignis:strategy", type.getStrategy());
            nbt.setInteger("ignis:version", 1);
        });

        return item;
    }
}
