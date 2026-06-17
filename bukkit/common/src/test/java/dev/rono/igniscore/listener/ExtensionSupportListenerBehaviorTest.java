package dev.rono.igniscore.listener;

import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import dev.rono.igniscore.service.BreakLoopTestSupport;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.TestDefinitions;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockbukkit.mockbukkit.block.BlockMock;
import org.mockbukkit.mockbukkit.entity.ItemMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionSupportListenerBehaviorTest extends MockBukkitTestBase {
    @TempDir
    Path tempDir;

    private BreakLoopTestSupport.Context ctx;

    @BeforeEach
    void setUpBreakLoop() throws Exception {
        ctx = BreakLoopTestSupport.create(
                plugin,
                server,
                platformHooks,
                tempDir,
                TestDefinitions.block("nuke"));
    }

    @Test
    void blockBreakDropsAreCollectedByRegisteredCollector() {
        PlayerMock player = server.addPlayer("miner");
        player.setGameMode(GameMode.SURVIVAL);
        Block block = world.getBlockAt(20, 64, 20);
        block.setType(Material.STONE);
        ((BlockMock) block).setDrops(List.of(new ItemStack(Material.COBBLESTONE)));
        IgnisLocation collectorAnchor = BukkitBridge.toIgnis(block.getLocation());
        AtomicBoolean collected = new AtomicBoolean(false);
        ctx.extensionSupport().registerDropCollector(collectorAnchor, (location, drops) -> {
            collected.set(true);
            drops.clear();
            return true;
        });

        BlockBreakEvent event = new BlockBreakEvent(block, player);
        ctx.extensionSupportListener().onBlockBreak(event);

        assertTrue(collected.get());
        assertFalse(event.isDropItems());
        assertEquals(0, event.getExpToDrop());
    }

    @Test
    void creativeBreakLeavesVanillaDropHandlingUntouched() {
        PlayerMock player = server.addPlayer("builder");
        player.setGameMode(GameMode.CREATIVE);
        Block block = world.getBlockAt(22, 64, 22);
        block.setType(Material.STONE);
        ((BlockMock) block).setDrops(List.of(new ItemStack(Material.COBBLESTONE)));
        AtomicBoolean collected = new AtomicBoolean(false);
        ctx.extensionSupport().registerDropCollector(BukkitBridge.toIgnis(block.getLocation()), (location, drops) -> {
            collected.set(true);
            return false;
        });

        BlockBreakEvent event = new BlockBreakEvent(block, player);
        ctx.extensionSupportListener().onBlockBreak(event);

        assertFalse(collected.get());
        assertTrue(event.isDropItems());
    }

    @Test
    void survivalBreakWithoutCollectorsDoesNotRehandleDrops() {
        PlayerMock player = server.addPlayer("miner");
        player.setGameMode(GameMode.SURVIVAL);
        Block block = world.getBlockAt(23, 64, 23);
        block.setType(Material.STONE);
        ((BlockMock) block).setDrops(List.of(new ItemStack(Material.COBBLESTONE)));

        BlockBreakEvent event = new BlockBreakEvent(block, player);
        ctx.extensionSupportListener().onBlockBreak(event);

        assertTrue(event.isDropItems());
    }

    @Test
    void itemSpawnIsCancelledWhenCollectorAbsorbsDrop() {
        IgnisLocation location = new IgnisLocation("world", 21, 64, 21);
        ctx.extensionSupport().registerDropCollector(location, (anchor, drops) -> {
            drops.clear();
            return true;
        });

        ItemMock item = new ItemMock(server, UUID.randomUUID(), new ItemStack(Material.COBBLESTONE));
        item.teleport(new org.bukkit.Location(world, 21, 64, 21));
        ItemSpawnEvent event = new ItemSpawnEvent(item);

        ctx.extensionSupportListener().onItemSpawn(event);

        assertTrue(event.isCancelled());
    }

    @Test
    void inventoryCloseRestoresCustomInventoryDecorations() {
        org.bukkit.inventory.Inventory inventory = server.createInventory(null, 9, "Cache");
        AtomicBoolean restored = new AtomicBoolean(false);
        AtomicBoolean closed = new AtomicBoolean(false);
        ctx.extensionSupport().registerCustomInventory(inventory, new dev.rono.igniscore.api.inventory.IgnisCustomInventory() {
            @Override
            public boolean accepts(IgnisItem stack) {
                return true;
            }

            @Override
            public void restoreDecorations() {
                restored.set(true);
            }

            @Override
            public boolean isSeparatorSlot(int slot) {
                return slot == 4;
            }

            @Override
            public void onClose() {
                closed.set(true);
            }
        });

        PlayerMock player = server.addPlayer("viewer");
        player.openInventory(inventory);
        ctx.extensionSupportListener().onInventoryClose(new InventoryCloseEvent(player.getOpenInventory()));

        assertNotNull(ctx.extensionSupport().getCustomInventory(inventory));
        assertTrue(restored.get());
        assertTrue(closed.get());
    }
}
