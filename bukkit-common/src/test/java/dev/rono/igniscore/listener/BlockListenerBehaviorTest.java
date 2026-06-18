package dev.rono.igniscore.listener;

import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import dev.rono.igniscore.service.BreakLoopTestSupport;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.TestDefinitions;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockListenerBehaviorTest extends MockBukkitTestBase {
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
                TestDefinitions.block("nuke"),
                TestDefinitions.breakableStorage());
    }

    @Test
    void blockBreakEventStartsCustomBreakAndCancelsVanilla() {
        PlayerMock player = server.addPlayer("breaker");
        player.setGameMode(GameMode.SURVIVAL);
        Block block = world.getBlockAt(10, 64, 10);
        BreakLoopTestSupport.placeCustomBlock(ctx.blockManager(), block, "nuke");

        BlockBreakEvent event = new BlockBreakEvent(block, player);
        ctx.blockListener().onBlockBreak(event);

        assertTrue(event.isCancelled());
        assertFalse(event.isDropItems());
    }

    @Test
    void leftClickRoutesToBreakService() {
        PlayerMock player = server.addPlayer("clicker");
        player.setGameMode(GameMode.CREATIVE);
        Block block = world.getBlockAt(11, 64, 11);
        BreakLoopTestSupport.placeCustomBlock(ctx.blockManager(), block, "storage");

        PlayerInteractEvent event = new PlayerInteractEvent(
                player,
                org.bukkit.event.block.Action.LEFT_CLICK_BLOCK,
                new ItemStack(Material.STONE),
                block,
                BlockFace.UP,
                EquipmentSlot.HAND);
        ctx.blockListener().onPlayerInteract(event);

        assertTrue(event.isCancelled());
        assertEquals(Material.AIR, block.getType());
        assertNull(ctx.blockManager().getPlacedBlockType(BukkitBridge.toIgnis(block.getLocation())));
    }

    @Test
    void rightClickWithFlintRoutesToIgniteService() {
        PlayerMock player = server.addPlayer("igniter");
        Block block = world.getBlockAt(12, 64, 12);
        BreakLoopTestSupport.placeCustomBlock(ctx.blockManager(), block, "nuke");
        ItemStack flint = new ItemStack(Material.FLINT_AND_STEEL);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player,
                org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK,
                flint,
                block,
                BlockFace.UP,
                EquipmentSlot.HAND);
        ctx.blockListener().onPlayerInteract(event);

        assertTrue(event.isCancelled());
        assertEquals(Material.AIR, block.getType());
        assertEquals(1, ctx.blockManager().getActiveBlocks().size());
    }

    @Test
    void blockDamageAbortCancelsMiningSession() {
        PlayerMock player = server.addPlayer("abort");
        player.setGameMode(GameMode.SURVIVAL);
        Block block = world.getBlockAt(13, 64, 13);
        BreakLoopTestSupport.placeCustomBlock(ctx.blockManager(), block, "storage");
        ctx.breakService().start(player, block, TestDefinitions.breakableStorage());

        ctx.blockListener().onBlockDamageAbort(
                new BlockDamageAbortEvent(player, block, new ItemStack(Material.AIR)));

        BreakLoopTestSupport.performTicks(server, 10);
        assertEquals(Material.BARRIER, block.getType());
    }
}
