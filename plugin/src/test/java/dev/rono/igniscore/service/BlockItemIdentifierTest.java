package dev.rono.igniscore.service;

import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.PdcBackedNbtService;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
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
        nbtService.editItem(item, nbt -> nbt.setString("ignis:block_id", "nuke"));

        assertEquals("nuke", identifier.resolveTypeId(item));
    }

    @Test
    void fallsBackToPersistentDataAndLegacyKey() {
        ItemStack modern = new ItemStack(Material.PAPER);
        ItemMeta modernMeta = modern.getItemMeta();
        modernMeta.getPersistentDataContainer().set(
                new NamespacedKey(plugin, "block_type"),
                PersistentDataType.STRING,
                "phantom");
        modern.setItemMeta(modernMeta);
        assertEquals("phantom", identifier.resolveTypeId(modern));

        ItemStack legacy = new ItemStack(Material.PAPER);
        ItemMeta legacyMeta = legacy.getItemMeta();
        legacyMeta.getPersistentDataContainer().set(
                new NamespacedKey("igniscore", "tnt_type"),
                PersistentDataType.STRING,
                "wormhole");
        legacy.setItemMeta(legacyMeta);
        assertEquals("wormhole", identifier.resolveTypeId(legacy));
    }

    @Test
    void returnsNullForUnknownOrAirItems() {
        assertNull(identifier.resolveTypeId(null));
        assertNull(identifier.resolveTypeId(new ItemStack(Material.AIR)));
        assertNull(identifier.resolveTypeId(new ItemStack(Material.STICK)));
    }
}
