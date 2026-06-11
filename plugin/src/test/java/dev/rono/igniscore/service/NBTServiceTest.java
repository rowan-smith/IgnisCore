package dev.rono.igniscore.service;

import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.PdcBackedNbtService;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        nbtService.editItem(item, nbt -> {
            nbt.setString("ignis:block_id", "nuke");
            nbt.setInteger("ignis:fuse", 80);
            nbt.setString("ignis:strategy", "nuclear");
        });

        assertEquals("nuke", nbtService.readItem(item, nbt -> nbt.getString("ignis:block_id")));
        assertEquals(80, nbtService.readItem(item, nbt -> nbt.getInteger("ignis:fuse")).intValue());
        assertEquals("nuclear", nbtService.readItem(item, nbt -> nbt.getString("ignis:strategy")));
    }

    @Test
    void ignoresAirAndNullItems() {
        nbtService.editItem(null, nbt -> nbt.setString("ignored", "true"));
        nbtService.editItem(new ItemStack(Material.AIR), nbt -> nbt.setString("ignored", "true"));

        assertNull(nbtService.readItem(null, nbt -> nbt.getString("ignored")));
        assertNull(nbtService.readItem(new ItemStack(Material.AIR), nbt -> nbt.getString("ignored")));
    }

    @Test
    void createsWritableCompound() {
        var compound = nbtService.createCompound();
        assertNotNull(compound);
        compound.setString("test", "value");
        assertEquals("value", compound.getString("test"));
    }
}
