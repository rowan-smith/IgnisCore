package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.inventory.ItemStack;

public class ItemIdentifier {
    private final IgnisNbtService nbtService;

    @Inject
    public ItemIdentifier(IgnisNbtService nbtService) {
        this.nbtService = nbtService;
    }

    public String resolveTypeId(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        String typeId = nbtService.getItemString(BukkitBridge.wrap(item), "ignis:item_id");
        return typeId == null || typeId.isBlank() ? null : typeId;
    }
}
