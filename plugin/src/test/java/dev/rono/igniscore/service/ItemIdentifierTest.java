package dev.rono.igniscore.service;

import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.PdcBackedNbtService;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemIdentifierTest extends MockBukkitTestBase {
    private ItemIdentifier identifier;
    private PdcBackedNbtService nbtService;

    @BeforeEach
    void setUpService() {
        nbtService = new PdcBackedNbtService();
        identifier = new ItemIdentifier(nbtService);
    }

    @Test
    void resolvesItemTypeFromNbt() {
        ItemStack item = new ItemStack(Material.SNOWBALL);
        nbtService.editItem(item, nbt -> nbt.setString("ignis:item_id", "grenade"));

        assertEquals("grenade", identifier.resolveTypeId(item));
    }

    @Test
    void returnsNullForMissingBlankOrAirItems() {
        ItemStack blank = new ItemStack(Material.SNOWBALL);
        nbtService.editItem(blank, nbt -> nbt.setString("ignis:item_id", ""));

        assertNull(identifier.resolveTypeId(null));
        assertNull(identifier.resolveTypeId(new ItemStack(Material.AIR)));
        assertNull(identifier.resolveTypeId(blank));
        assertNull(identifier.resolveTypeId(new ItemStack(Material.SNOWBALL)));
    }
}
