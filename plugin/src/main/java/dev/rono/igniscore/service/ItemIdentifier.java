package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.service.IgnisNbtService;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemIdentifier {
    private final IgnisNbtService nbtService;

    @Inject
    public ItemIdentifier(IgnisNbtService nbtService) {
        this.nbtService = nbtService;
    }

    public String resolveTypeId(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return null;
        }

        String typeId = nbtService.readItem(item, nbt -> nbt.getString("ignis:item_id"));
        if (typeId == null || typeId.isEmpty()) {
            return null;
        }
        return typeId;
    }
}
