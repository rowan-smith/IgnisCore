package dev.rono.igniscore.service;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.TestDefinitions;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomBlockIgnitionServiceBehaviorTest extends MockBukkitTestBase {
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
    void igniteRemovesBarrierAndTriggersActiveBlock() {
        Block block = world.getBlockAt(2, 64, 2);
        BreakLoopTestSupport.placeCustomBlock(ctx.blockManager(), block, "nuke");

        ctx.ignitionService().ignite(block, TestDefinitions.block("nuke"), null, null);

        assertEquals(Material.AIR, block.getType());
        assertNull(ctx.blockManager().getPlacedBlockType(BukkitBridge.toIgnis(block.getLocation())));
        assertEquals(1, ctx.blockManager().getActiveBlocks().size());
    }

    @Test
    void flintAndSteelTakesDamageInSurvival() {
        PlayerMock player = server.addPlayer("pyro");
        player.setGameMode(GameMode.SURVIVAL);
        Block block = world.getBlockAt(3, 64, 3);
        BreakLoopTestSupport.placeCustomBlock(ctx.blockManager(), block, "nuke");
        ItemStack flint = new ItemStack(Material.FLINT_AND_STEEL);

        ctx.ignitionService().ignite(block, TestDefinitions.block("nuke"), player, flint);

        assertTrue(flint.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable damageable
                && damageable.getDamage() > 0);
    }
}
