package dev.rono.igniscore.service;

import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.PdcBackedNbtService;
import dev.rono.igniscore.support.TestDefinitions;
import dev.rono.igniscore.support.StubBlockManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BlockItemFactoryTest extends MockBukkitTestBase {
    private BlockItemFactory factory;
    private PdcBackedNbtService nbtService;
    private BlockItemIdentifier identifier;

    @BeforeEach
    void setUpFactory() {
        nbtService = new PdcBackedNbtService();
        StubBlockManager blockManager = StubBlockManager.with(TestDefinitions.block("nuke"));
        factory = new BlockItemFactory(blockManager, nbtService, platformHooks);
        identifier = new BlockItemIdentifier(plugin, nbtService);
    }

    @Test
    void createsConfiguredBlockItemStack() {
        ItemStack item = factory.createBlockItem("nuke");

        assertEquals(Material.PAPER, item.getType());
        assertEquals("nuke", identifier.resolveTypeId(item));
        assertEquals(10001, platformHooks.readCustomModelData(item).orElseThrow());
        assertEquals(80, nbtService.getItemInt(BukkitBridge.wrap(item), "ignis:fuse", 0));
        assertEquals("nuke", nbtService.getItemString(BukkitBridge.wrap(item), "ignis:extension_id"));
        if (item.getItemMeta().hasItemModel()) {
            assertEquals("nuke", item.getItemMeta().getItemModel().getKey());
        }
    }

    @Test
    void rejectsUnknownBlockTypes() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> factory.createBlockItem("missing"));
        assertEquals("Unknown block type: missing", error.getMessage());
    }
}
