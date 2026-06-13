package dev.rono.igniscore.service;

import dev.rono.igniscore.manager.ItemManager;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.PdcBackedNbtService;
import dev.rono.igniscore.support.TestDefinitions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URLClassLoader;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemFactoryTest extends MockBukkitTestBase {
    private ItemFactory factory;
    private ItemIdentifier identifier;
    private PdcBackedNbtService nbtService;

    @BeforeEach
    void setUpFactory() throws Exception {
        nbtService = new PdcBackedNbtService();
        ItemManager itemManager = new ItemManager();
        try (URLClassLoader classLoader = new URLClassLoader(new java.net.URL[0], getClass().getClassLoader())) {
            itemManager.loadFromExtensions(List.of(
                    TestDefinitions.loadedItem(TestDefinitions.item("grenade"), classLoader)));
        }
        factory = new ItemFactory(itemManager, nbtService, platformHooks);
        identifier = new ItemIdentifier(nbtService);
    }

    @Test
    void createsConfiguredItemStack() {
        ItemStack item = factory.createItem("grenade");

        assertEquals(Material.SNOWBALL, item.getType());
        assertEquals("grenade", identifier.resolveTypeId(item));
        assertEquals(20001, platformHooks.readCustomModelData(item).orElseThrow());
        assertEquals("grenade", nbtService.getItemString(BukkitBridge.wrap(item), "ignis:extension_id"));
        if (item.getItemMeta().hasItemModel()) {
            assertEquals("grenade", item.getItemMeta().getItemModel().getKey());
        }
    }

    @Test
    void rejectsUnknownItemTypes() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> factory.createItem("missing"));
        assertEquals("Unknown item type: missing", error.getMessage());
    }
}
