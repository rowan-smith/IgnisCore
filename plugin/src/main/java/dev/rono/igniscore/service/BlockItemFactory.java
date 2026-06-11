package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.api.model.BlockDefinition;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockItemFactory {
    private final BlockManager blockManager;
    private final IgnisNbtService nbtService;

    @Inject
    public BlockItemFactory(BlockManager blockManager, IgnisNbtService nbtService) {
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
            meta.setItemModel(new NamespacedKey("igniscore", type.getId()));
            item.setItemMeta(meta);
        }

        item.setData(
                DataComponentTypes.CUSTOM_MODEL_DATA,
                CustomModelData.customModelData().addFloat((float) type.getCustomModelData()).build()
        );

        nbtService.editItem(item, nbt -> {
            nbt.setString("ignis:block_id", typeId);
            nbt.setString("ignis:strategy", type.getStrategy());
            nbt.setInteger("ignis:version", 1);
            nbt.setInteger("ignis:fuse", StrategySupport.fuse(type, 80));
        });

        return item;
    }
}
