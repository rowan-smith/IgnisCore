package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.platform.PlatformHooks;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFactory {
    private final ItemManager itemManager;
    private final IgnisNbtService nbtService;
    private final PlatformHooks platformHooks;

    @Inject
    public ItemFactory(ItemManager itemManager, IgnisNbtService nbtService, PlatformHooks platformHooks) {
        this.itemManager = itemManager;
        this.nbtService = nbtService;
        this.platformHooks = platformHooks;
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
            platformHooks.applyItemMeta(
                    meta,
                    type.getTitle(),
                    type.getDescription(),
                    new NamespacedKey("igniscore", type.getId())
            );
            item.setItemMeta(meta);
        }

        platformHooks.applyCustomModelData(item, type.getCustomModelData());

        nbtService.editItem(item, nbt -> {
            nbt.setString("ignis:item_id", typeId);
            nbt.setString("ignis:strategy", type.getStrategy());
            nbt.setInteger("ignis:version", 1);
        });

        return item;
    }
}
