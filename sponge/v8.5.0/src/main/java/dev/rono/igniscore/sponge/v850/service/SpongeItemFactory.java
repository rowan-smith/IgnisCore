package dev.rono.igniscore.sponge.v850.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.PlatformAdapter;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.sponge.v850.adapter.SpongeBridge;
import dev.rono.igniscore.sponge.v850.support.SpongeRegistrySupport;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Locale;

public class SpongeItemFactory {
    private final PlatformAdapter platformAdapter;
    private final IgnisNbtService nbtService;
    private final ItemManager itemManager;

    @Inject
    public SpongeItemFactory(PlatformAdapter platformAdapter,
                             IgnisNbtService nbtService,
                             ItemManager itemManager) {
        this.platformAdapter = platformAdapter;
        this.nbtService = nbtService;
        this.itemManager = itemManager;
    }

    public ItemStack createItem(String typeId) {
        ItemDefinition type = itemManager.getItemTypes().get(typeId);
        if (type == null) {
            throw new IllegalArgumentException("Unknown item type: " + typeId);
        }

        ResourceKey materialKey = ResourceKey.resolve(type.getBaseMaterial().toLowerCase(Locale.ROOT));
        var itemType = SpongeRegistrySupport.findItemType(materialKey)
                .orElse(ItemTypes.STONE.get());

        ItemStack itemStack = ItemStack.of(itemType, 1);
        var ignisItem = SpongeBridge.wrap(itemStack);
        platformAdapter.applyCustomModelData(itemStack, type.getCustomModelData());
        nbtService.setItemString(ignisItem, "ignis:item_id", typeId);
        nbtService.setItemString(ignisItem, "ignis:extension_id", type.getExtensionId());
        return SpongeBridge.unwrap(ignisItem);
    }
}
