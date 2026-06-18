package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.manager.BlockDefinitionLookup;
import dev.rono.igniscore.manager.PlacedBlockRegistry;
import dev.rono.igniscore.platform.PlatformHooks;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockItemFactory {
    private final BlockDefinitionLookup blockRegistry;
    private final IgnisNbtService nbtService;
    private final PlatformHooks platformHooks;

    @Inject
    public BlockItemFactory(BlockDefinitionLookup blockRegistry, IgnisNbtService nbtService, PlatformHooks platformHooks) {
        this.blockRegistry = blockRegistry;
        this.nbtService = nbtService;
        this.platformHooks = platformHooks;
    }

    public ItemStack createBlockItem(String typeId) {
        BlockDefinition type = blockRegistry.getBlockTypes().get(typeId);
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
            platformHooks.applyItemMeta(
                    meta,
                    type.getTitle(),
                    type.getDescription(),
                    new NamespacedKey("igniscore", type.getId())
            );
            item.setItemMeta(meta);
        }

        platformHooks.applyCustomModelData(item, type.getCustomModelData());

        var ignisItem = BukkitBridge.wrap(item);
        nbtService.setItemString(ignisItem, "ignis:block_id", typeId);
        nbtService.setItemString(ignisItem, "ignis:extension_id", type.getExtensionId());
        nbtService.setItemInt(ignisItem, "ignis:version", 1);
        nbtService.setItemInt(ignisItem, "ignis:fuse", StrategySupport.customInt(type, "fuse", 0));

        return item;
    }
}
