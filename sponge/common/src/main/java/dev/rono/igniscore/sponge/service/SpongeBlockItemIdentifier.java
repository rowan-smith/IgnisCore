package dev.rono.igniscore.sponge.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.sponge.adapter.SpongeBridge;
import org.spongepowered.api.item.inventory.ItemStack;

public class SpongeBlockItemIdentifier {
    private final IgnisNbtService nbtService;

    @Inject
    public SpongeBlockItemIdentifier(IgnisNbtService nbtService) {
        this.nbtService = nbtService;
    }

    public String resolveTypeId(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return null;
        }
        String typeId = nbtService.getItemString(SpongeBridge.wrap(item), "ignis:block_id");
        return typeId == null || typeId.isBlank() ? null : typeId;
    }
}
