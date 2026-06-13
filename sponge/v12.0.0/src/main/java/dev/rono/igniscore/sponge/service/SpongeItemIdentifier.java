package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpongeItemIdentifier {
    private final IgnisNbtService nbtService;

    @Inject
    public SpongeItemIdentifier(IgnisNbtService nbtService) {
        this.nbtService = nbtService;
    }

    public String resolveTypeId(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return null;
        }
        String typeId = nbtService.getItemString(SpongeBridge.wrap(item), "ignis:item_id");
        return typeId == null || typeId.isBlank() ? null : typeId;
    }
}
