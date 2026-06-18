package dev.rono.igniscore.spigot.adapter;

import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.support.MockBukkitTestBase;
import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BukkitBridgeBehaviorTest extends MockBukkitTestBase {

    @Test
    void locationRoundTripPreservesCoordinates() {
        Location original = new Location(world, 1.25, 64.5, -2.75, 90f, 15f);

        IgnisLocation ignis = BukkitBridge.toIgnis(original);
        Location restored = BukkitBridge.toBukkit(ignis);

        assertEquals(original.getX(), restored.getX(), 0.001);
        assertEquals(original.getY(), restored.getY(), 0.001);
        assertEquals(original.getZ(), restored.getZ(), 0.001);
        assertEquals(original.getYaw(), restored.getYaw(), 0.001);
        assertEquals(world.getUID(), ignis.worldId());
        assertEquals(world.getName(), ignis.worldName());
    }

    @Test
    void mapsBukkitActionsToIgnisInteractions() {
        assertEquals(IgnisInteraction.LEFT_CLICK_BLOCK, BukkitBridge.toIgnisInteraction(Action.LEFT_CLICK_BLOCK));
        assertEquals(IgnisInteraction.RIGHT_CLICK_AIR, BukkitBridge.toIgnisInteraction(Action.RIGHT_CLICK_AIR));
        assertEquals(IgnisInteraction.PHYSICAL, BukkitBridge.toIgnisInteraction(Action.PHYSICAL));
        assertEquals(IgnisInteraction.RIGHT_CLICK_AIR, BukkitBridge.toIgnisInteraction(null));
    }
}
