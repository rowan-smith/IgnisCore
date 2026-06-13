package dev.rono.igniscore.service;

import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.PdcBackedNbtService;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NBTServiceTest extends MockBukkitTestBase {
    private PdcBackedNbtService nbtService;

    @BeforeEach
    void setUpService() {
        nbtService = new PdcBackedNbtService();
    }

    @Test
    void editsAndReadsItemNbt() {
        ItemStack item = new ItemStack(Material.PAPER);
        var ignisItem = BukkitBridge.wrap(item);

        nbtService.setItemString(ignisItem, "ignis:block_id", "nuke");
        nbtService.setItemInt(ignisItem, "ignis:fuse", 80);
        nbtService.setItemString(ignisItem, "ignis:extension_id", "nuke");

        assertEquals("nuke", nbtService.getItemString(ignisItem, "ignis:block_id"));
        assertEquals(80, nbtService.getItemInt(ignisItem, "ignis:fuse", 0));
        assertEquals("nuke", nbtService.getItemString(ignisItem, "ignis:extension_id"));
    }

    @Test
    void ignoresAirAndNullItems() {
        nbtService.setItemString(null, "ignored", "true");
        nbtService.setItemString(BukkitBridge.wrap(new ItemStack(Material.AIR)), "ignored", "true");

        assertNull(nbtService.getItemString(null, "ignored"));
        assertNull(nbtService.getItemString(BukkitBridge.wrap(new ItemStack(Material.AIR)), "ignored"));
    }
}
