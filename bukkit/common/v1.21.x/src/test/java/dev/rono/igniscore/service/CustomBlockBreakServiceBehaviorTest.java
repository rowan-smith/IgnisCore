package dev.rono.igniscore.service;

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.TestDefinitions;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CustomBlockBreakServiceBehaviorTest extends MockBukkitTestBase {
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
    void creativeModeBreaksInstantlyWithoutDrop() {
        PlayerMock player = server.addPlayer("miner");
        player.setGameMode(GameMode.CREATIVE);
        Block block = world.getBlockAt(4, 64, 4);
        BreakLoopTestSupport.placeCustomBlock(ctx.blockManager(), block, "storage");

        ctx.breakService().start(player, block, TestDefinitions.breakableStorage());

        assertEquals(Material.AIR, block.getType());
        assertNull(ctx.blockManager().getPlacedBlockType(BukkitBridge.toIgnis(block.getLocation())));
    }

    @Test
    void nonBreakableBlockIsIgnored() {
        BlockDefinition unbreakable = new BlockDefinition(
                "vault",
                "paper",
                "carrot_on_a_stick",
                net.kyori.adventure.text.Component.text("vault"),
                java.util.List.of(),
                true,
                false,
                "top.png",
                "side.png",
                "bottom.png",
                java.util.Map.of(),
                java.util.Map.of(),
                java.util.Map.of(),
                java.util.Map.of(),
                10003,
                false,
                false,
                false,
                "vault");
        PlayerMock player = server.addPlayer("miner");
        Block block = world.getBlockAt(5, 64, 5);
        BreakLoopTestSupport.placeCustomBlock(ctx.blockManager(), block, "storage");
        block.setType(Material.BARRIER);

        ctx.breakService().start(player, block, unbreakable);

        assertEquals(Material.BARRIER, block.getType());
    }

    @Test
    void combustibleBlockBreaksInstantlyInSurvival() {
        PlayerMock player = server.addPlayer("miner");
        player.setGameMode(GameMode.SURVIVAL);
        Block block = world.getBlockAt(6, 64, 6);
        BreakLoopTestSupport.placeCustomBlock(ctx.blockManager(), block, "nuke");

        ctx.breakService().start(player, block, TestDefinitions.block("nuke"));

        assertEquals(Material.AIR, block.getType());
        assertNull(ctx.blockManager().getPlacedBlockType(BukkitBridge.toIgnis(block.getLocation())));
    }

    @Test
    void breakableBlockMiningCompletesAfterConfiguredTicks() {
        PlayerMock player = server.addPlayer("miner");
        player.setGameMode(GameMode.SURVIVAL);
        Block block = world.getBlockAt(7, 64, 7);
        BreakLoopTestSupport.placeCustomBlock(ctx.blockManager(), block, "storage");

        ctx.breakService().start(player, block, TestDefinitions.breakableStorage());
        assertEquals(Material.BARRIER, block.getType());

        BreakLoopTestSupport.performTicks(server, 6);

        assertEquals(Material.AIR, block.getType());
        assertNull(ctx.blockManager().getPlacedBlockType(BukkitBridge.toIgnis(block.getLocation())));
    }

    @Test
    void cancelClearsMiningSession() {
        PlayerMock player = server.addPlayer("miner");
        player.setGameMode(GameMode.SURVIVAL);
        Block block = world.getBlockAt(8, 64, 8);
        BreakLoopTestSupport.placeCustomBlock(ctx.blockManager(), block, "storage");

        ctx.breakService().start(player, block, TestDefinitions.breakableStorage());
        ctx.breakService().cancel(player.getUniqueId());
        BreakLoopTestSupport.performTicks(server, 10);

        assertEquals(Material.BARRIER, block.getType());
    }
}
