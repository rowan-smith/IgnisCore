package dev.rono.igniscore.service;

import dev.rono.igniscore.spigot.adapter.BukkitBridge;
import dev.rono.igniscore.support.MockBukkitTestBase;
import dev.rono.igniscore.support.PdcBackedNbtService;
import dev.rono.igniscore.support.TestDefinitions;
import dev.rono.igniscore.support.StubBlockManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomBlockPlacementServiceTest extends MockBukkitTestBase {
    private CustomBlockPlacementService placementService;
    private StubBlockManager blockManager;
    private BlockItemFactory blockItemFactory;
    private PdcBackedNbtService nbtService;

    @BeforeEach
    void setUpService() {
        nbtService = new PdcBackedNbtService();
        blockManager = StubBlockManager.with(TestDefinitions.block("nuke"));
        blockItemFactory = new BlockItemFactory(blockManager, nbtService, platformHooks);
        placementService = new CustomBlockPlacementService(
                plugin, blockManager, new BlockItemIdentifier(plugin, nbtService), platformHooks);
    }

    @Test
    void placesCustomBlockOnRightClick() {
        PlayerMock player = server.addPlayer("placer");
        player.setGameMode(GameMode.SURVIVAL);
        Block clicked = world.getBlockAt(0, 64, 0);
        clicked.setType(Material.STONE);
        world.getBlockAt(0, 65, 0).setType(Material.AIR);

        ItemStack item = blockItemFactory.createBlockItem("nuke");
        item.setAmount(2);
        player.getInventory().setItemInMainHand(item);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.RIGHT_CLICK_BLOCK, item, clicked, BlockFace.UP, EquipmentSlot.HAND);
        placementService.handleInteractPlacement(event);

        assertTrue(event.isCancelled());
        assertEquals(Material.BARRIER, world.getBlockAt(0, 65, 0).getType());
        assertEquals("nuke", blockManager.getPlacedBlockType(BukkitBridge.toIgnis(world.getBlockAt(0, 65, 0).getLocation())));
        assertEquals(1, event.getItem().getAmount());
    }

    @Test
    void ignoresUnknownItemsAndNonPlaceableFaces() {
        PlayerMock player = server.addPlayer("ignored");
        Block clicked = world.getBlockAt(2, 64, 0);
        clicked.setType(Material.STONE);
        world.getBlockAt(2, 65, 0).setType(Material.BEDROCK);

        ItemStack item = new ItemStack(Material.STICK);
        player.getInventory().setItemInMainHand(item);

        PlayerInteractEvent event = new PlayerInteractEvent(
                player, Action.RIGHT_CLICK_BLOCK, item, clicked, BlockFace.UP, EquipmentSlot.HAND);
        placementService.handleInteractPlacement(event);

        assertFalse(event.isCancelled());
        assertEquals(Material.BEDROCK, world.getBlockAt(2, 65, 0).getType());
    }

    @Test
    void registersKnownBlockPlaceEvents() {
        PlayerMock player = server.addPlayer("builder");
        Block block = world.getBlockAt(4, 64, 0);
        block.setType(Material.AIR);
        ItemStack item = blockItemFactory.createBlockItem("nuke");

        BlockPlaceEvent event = new BlockPlaceEvent(block, block.getState(), block, item, player, true, EquipmentSlot.HAND);
        placementService.handleBlockPlace(event);

        assertEquals("nuke", blockManager.getPlacedBlockType(BukkitBridge.toIgnis(block.getLocation())));
    }
}
