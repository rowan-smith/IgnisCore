package dev.rono.igniscore.service;

import com.google.inject.Inject;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class BlockItemIdentifier {
    private final Plugin plugin;
    private final IgnisNbtService nbtService;

    @Inject
    public BlockItemIdentifier(Plugin plugin, IgnisNbtService nbtService) {
        this.plugin = plugin;
        this.nbtService = nbtService;
    }

    public String resolveTypeId(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        String typeId = nbtService.getItemString(BukkitBridge.wrap(item), "ignis:block_id");
        return typeId == null || typeId.isBlank() ? null : typeId;
    }
}
