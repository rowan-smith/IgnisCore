package dev.rono.igniscore.service;

import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.model.BlockDefinition;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.List;

public class BlockItemFactory {
    private final BlockManager blockManager;
    private final NBTService nbtService;

    public BlockItemFactory(BlockManager blockManager, NBTService nbtService) {
        this.blockManager = blockManager;
        this.nbtService = nbtService;
    }

    public ItemStack createBlockItem(String typeId) {
        BlockDefinition type = blockManager.getBlockTypes().get(typeId);
        if (type == null) {
            throw new IllegalArgumentException("Unknown block type: " + typeId);
        }

        Material material = Material.matchMaterial(type.getBaseMaterial());
        if (material == null) {
            material = Material.CARROT_ON_A_STICK;
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
            nbt.setString("ignis:block_id", typeId);
            nbt.setString("ignis:strategy", type.getStrategy());
            nbt.setInteger("ignis:version", 1);
            nbt.setInteger("ignis:fuse", type.getFuse());
        });

        return item;
    }
}
