package dev.rono.igniscore.block.quarrycache;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.service.ExtensionSupportService;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.PdcBackedNbtService;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuarryCachePersistenceTest extends MockBukkitTestBase {
    @Test
    void rePlaceRestoresContentsFromLocationFileWhenItemAttachFails() {
        ExtensionSupportService extensionSupport = new ExtensionSupportService();
        IgnisStrategyContext context = new IgnisStrategyContext(plugin, null, null, null, extensionSupport);
        QuarryCacheRegistry registry = new QuarryCacheRegistry(context);
        BlockDefinition definition = quarryDefinition();
        Location location = new Location(world, 12, 64, 12);

        registry.register(location, definition, null);
        List<ItemStack> drops = new ArrayList<>(List.of(new ItemStack(Material.COBBLESTONE, 8)));
        assertTrue(extensionSupport.tryCollect(location, drops));
        assertTrue(drops.isEmpty());

        ItemStack droppedItem = new ItemStack(Material.PAPER);
        registry.handleBreak(location, droppedItem);

        registry.register(location, definition, null);

        Player player = server.addPlayer();
        registry.openGui(player, location);
        ItemStack stored = player.getOpenInventory().getTopInventory()
                .getItem(QuarryCacheInventory.STORAGE_START);
        assertEquals(Material.COBBLESTONE, stored.getType());
        assertEquals(8, stored.getAmount());
    }

    @Test
    void breakAndReplaceRoundTripsContentsThroughItemNbt() {
        ExtensionSupportService extensionSupport = new ExtensionSupportService();
        PdcBackedNbtService nbtService = new PdcBackedNbtService();
        IgnisStrategyContext context = new IgnisStrategyContext(plugin, nbtService, null, null, extensionSupport);
        QuarryCacheRegistry registry = new QuarryCacheRegistry(context);
        BlockDefinition definition = quarryDefinition();
        Location location = new Location(world, 20, 64, 20);

        registry.register(location, definition, null);
        List<ItemStack> drops = new ArrayList<>(List.of(new ItemStack(Material.IRON_INGOT, 4)));
        assertTrue(extensionSupport.tryCollect(location, drops));

        ItemStack droppedItem = new ItemStack(Material.PAPER);
        registry.handleBreak(location, droppedItem);
        assertTrue(new QuarryCacheStorage(plugin, nbtService).hasStoredContents(droppedItem));

        Location replacement = new Location(world, 21, 64, 21);
        registry.register(replacement, definition, droppedItem);

        Player player = server.addPlayer();
        registry.openGui(player, replacement);
        ItemStack stored = player.getOpenInventory().getTopInventory()
                .getItem(QuarryCacheInventory.STORAGE_START);
        assertEquals(Material.IRON_INGOT, stored.getType());
        assertEquals(4, stored.getAmount());
    }

    private BlockDefinition quarryDefinition() {
        return new BlockDefinition(
                "quarry-cache",
                "paper",
                "carrot_on_a_stick",
                Component.text("Quarry Cache"),
                List.of(),
                true,
                true,
                "top.jpg",
                "side.jpg",
                "bottom.jpg",
                Map.of("collectRadius", 5.0, "collectDepth", 5.0, "showCollectZone", false),
                Map.of(),
                Map.of(),
                Map.of(),
                10001,
                false,
                false,
                false,
                "quarry-cache-block"
        );
    }
}
