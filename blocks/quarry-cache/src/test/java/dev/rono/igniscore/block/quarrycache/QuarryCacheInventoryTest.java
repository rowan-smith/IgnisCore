package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.testsupport.MockBukkitExtensionTestBase;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuarryCacheInventoryTest extends MockBukkitExtensionTestBase {
    @Test
    void filterRowUsesCenterSlotsWithDecoratorsOnSides() {
        QuarryCacheInventory inventory = new QuarryCacheInventory(
                new Location(world, 0, 64, 0),
                Component.text("Quarry Cache"));

        assertTrue(inventory.isSeparatorSlot(0));
        assertTrue(inventory.isSeparatorSlot(1));
        assertFalse(inventory.isSeparatorSlot(2));
        assertTrue(QuarryCacheInventory.isFilterSlot(2));
        assertTrue(QuarryCacheInventory.isFilterSlot(6));
        assertFalse(QuarryCacheInventory.isFilterSlot(7));
        assertTrue(inventory.isSeparatorSlot(7));
        assertTrue(inventory.isSeparatorSlot(8));
    }

    @Test
    void acceptsOnlyMatchingItemsWhenFiltersAreSet() {
        QuarryCacheInventory inventory = new QuarryCacheInventory(
                new Location(world, 0, 64, 0),
                Component.text("Quarry Cache"));

        inventory.getInventory().setItem(QuarryCacheInventory.FILTER_START, new ItemStack(Material.IRON_ORE));
        inventory.getInventory().setItem(QuarryCacheInventory.FILTER_START + 1, new ItemStack(Material.COAL));

        assertTrue(inventory.accepts(new ItemStack(Material.IRON_ORE)));
        assertTrue(inventory.accepts(new ItemStack(Material.COAL)));
        assertFalse(inventory.accepts(new ItemStack(Material.DIRT)));
    }
}
