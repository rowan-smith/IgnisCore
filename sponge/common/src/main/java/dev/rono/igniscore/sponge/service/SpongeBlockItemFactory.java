package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.manager.BlockDefinitionLookup;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.support.SpongeRegistrySupport;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Locale;

public class SpongeBlockItemFactory {
    private final BlockDefinitionLookup blockRegistry;
    private final IgnisNbtService nbtService;
    private final PlatformAdapter platformAdapter;

    @Inject
    public SpongeBlockItemFactory(BlockDefinitionLookup blockRegistry,
                                  IgnisNbtService nbtService,
                                  PlatformAdapter platformAdapter) {
        this.blockRegistry = blockRegistry;
        this.nbtService = nbtService;
        this.platformAdapter = platformAdapter;
    }

    public ItemStack createBlockItem(String typeId) {
        BlockDefinition type = blockRegistry.getBlockTypes().get(typeId);
        if (type == null) {
            throw new IllegalArgumentException("Unknown block type: " + typeId);
        }

        ResourceKey materialKey = ResourceKey.resolve(type.getBaseMaterial().toLowerCase(Locale.ROOT));
        var itemType = SpongeRegistrySupport.findItemType(materialKey)
                .orElse(ItemTypes.CARROT_ON_A_STICK.get());

        ItemStack item = ItemStack.of(itemType, 1);
        platformAdapter.applyItemMeta(
                item,
                type.getTitle(),
                type.getDescription(),
                "igniscore:" + type.getId());
        platformAdapter.applyCustomModelData(item, type.getCustomModelData());

        var ignisItem = SpongeBridge.wrap(item);
        nbtService.setItemString(ignisItem, "ignis:block_id", typeId);
        nbtService.setItemString(ignisItem, "ignis:extension_id", type.getExtensionId());
        nbtService.setItemInt(ignisItem, "ignis:version", 1);
        nbtService.setItemInt(ignisItem, "ignis:fuse", StrategySupport.customInt(type, "fuse", 0));

        return SpongeBridge.unwrap(ignisItem);
    }
}
