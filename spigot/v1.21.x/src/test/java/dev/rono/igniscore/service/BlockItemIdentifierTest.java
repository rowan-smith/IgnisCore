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

class BlockItemIdentifierTest extends MockBukkitTestBase {
    private BlockItemIdentifier identifier;
    private PdcBackedNbtService nbtService;

    @BeforeEach
    void setUpService() {
        nbtService = new PdcBackedNbtService();
        identifier = new BlockItemIdentifier(plugin, nbtService);
    }

    @Test
    void resolvesTypeFromNbtFirst() {
        ItemStack item = new ItemStack(Material.PAPER);
        nbtService.setItemString(BukkitBridge.wrap(item), "ignis:block_id", "nuke");

        assertEquals("nuke", identifier.resolveTypeId(item));
    }

    @Test
    void returnsNullForUnknownOrAirItems() {
        assertNull(identifier.resolveTypeId(null));
        assertNull(identifier.resolveTypeId(new ItemStack(Material.AIR)));
        assertNull(identifier.resolveTypeId(new ItemStack(Material.STICK)));
    }
}
