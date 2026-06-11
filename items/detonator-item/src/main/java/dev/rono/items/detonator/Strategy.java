package dev.rono.items.detonator;

import de.tr7zw.nbtapi.iface.ReadWriteItemNBT;
import de.tr7zw.nbtapi.iface.ReadableItemNBT;
import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Strategy extends AbstractIgnisItemStrategy {
    private static final String LINKED_BOMBS_KEY = "ignis:linked_bombs";

    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public void onItemUse(Player player, ItemDefinition definition, ItemStack item, Action action, Block clickedBlock) {
        if (action == Action.LEFT_CLICK_BLOCK) {
            assignBomb(player, definition, item, clickedBlock);
            return;
        }

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            detonateLinkedBombs(player, definition, item);
        }
    }

    private void assignBomb(Player player, ItemDefinition definition, ItemStack item, Block clickedBlock) {
        if (clickedBlock == null) {
            return;
        }

        Location location = clickedBlock.getLocation();
        String blockType = IgnisCoreAPI.getPlacedBlockType(location);
        if (!isTargetBlock(definition, blockType)) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
            player.sendMessage("§cThat block cannot be linked to this detonator.");
            return;
        }

        String encoded = encodeLocation(location);
        List<String> linkedBombs = readLinkedBombs(item);
        if (linkedBombs.contains(encoded)) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
            player.sendMessage("§eThis bomb is already linked.");
            return;
        }

        int maxLinks = getCustomInt(definition, "max_links", 16);
        if (linkedBombs.size() >= maxLinks) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.4f);
            player.sendMessage("§cThis detonator is full. Detonate or clear links first.");
            return;
        }

        linkedBombs.add(encoded);
        writeLinkedBombs(item, linkedBombs);

        Location center = location.toCenterLocation();
        center.getWorld().playSound(center, Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.6f);
        center.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, center, 8, 0.25, 0.25, 0.25, 0.0);
        player.sendMessage("§aRemote bomb linked. §7(" + linkedBombs.size() + "/" + maxLinks + ")");
    }

    private void detonateLinkedBombs(Player player, ItemDefinition definition, ItemStack item) {
        List<String> linkedBombs = readLinkedBombs(item);
        if (linkedBombs.isEmpty()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
            player.sendMessage("§cNo linked bombs. Left-click a remote bomb to assign one.");
            return;
        }

        int triggered = 0;
        Iterator<String> iterator = linkedBombs.iterator();
        while (iterator.hasNext()) {
            String encoded = iterator.next();
            Location location = decodeLocation(encoded);
            if (location == null) {
                iterator.remove();
                continue;
            }

            String blockType = IgnisCoreAPI.getPlacedBlockType(location);
            if (!isTargetBlock(definition, blockType)) {
                iterator.remove();
                continue;
            }

            IgnisCoreAPI.ignitePlacedBlock(location, player);
            iterator.remove();
            triggered++;
        }

        writeLinkedBombs(item, linkedBombs);

        if (triggered > 0) {
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.2f);
            player.sendMessage("§cDetonating §f" + triggered + "§c linked bomb" + (triggered == 1 ? "" : "s") + ".");
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
        player.sendMessage("§cNo linked bombs remain in range.");
    }

    private boolean isTargetBlock(ItemDefinition definition, String blockType) {
        if (blockType == null) {
            return false;
        }

        Object configured = definition.getCustomData().get("target_blocks");
        if (configured instanceof List<?> targets) {
            for (Object target : targets) {
                if (target != null && blockType.equalsIgnoreCase(target.toString())) {
                    return true;
                }
            }
            return false;
        }

        Object singleTarget = definition.getCustomData().get("target_block");
        if (singleTarget != null) {
            return blockType.equalsIgnoreCase(singleTarget.toString());
        }

        return "remote-bomb".equalsIgnoreCase(blockType);
    }

    private List<String> readLinkedBombs(ItemStack item) {
        List<String> linked = context.getNbtService().readItem(item, this::readLinkedBombs);
        return linked == null ? new ArrayList<>() : new ArrayList<>(linked);
    }

    private List<String> readLinkedBombs(ReadableItemNBT nbt) {
        if (nbt == null || !nbt.hasNBTData()) {
            return new ArrayList<>();
        }

        try {
            List<String> values = nbt.getStringList(LINKED_BOMBS_KEY);
            return values == null ? new ArrayList<>() : new ArrayList<>(values);
        } catch (Exception ignored) {
            String legacy = nbt.getString(LINKED_BOMBS_KEY);
            if (legacy == null || legacy.isBlank()) {
                return new ArrayList<>();
            }
            return new ArrayList<>(List.of(legacy.split("\\|")));
        }
    }

    private void writeLinkedBombs(ItemStack item, List<String> linkedBombs) {
        context.getNbtService().editItem(item, nbt -> writeLinkedBombs(nbt, linkedBombs));
    }

    private void writeLinkedBombs(ReadWriteItemNBT nbt, List<String> linkedBombs) {
        if (linkedBombs.isEmpty()) {
            nbt.removeKey(LINKED_BOMBS_KEY);
            return;
        }

        nbt.setStringList(LINKED_BOMBS_KEY, linkedBombs);
    }

    private String encodeLocation(Location location) {
        return location.getWorld().getUID()
                + ":" + location.getBlockX()
                + ":" + location.getBlockY()
                + ":" + location.getBlockZ();
    }

    private Location decodeLocation(String encoded) {
        String[] parts = encoded.split(":");
        if (parts.length != 4) {
            return null;
        }

        try {
            World world = Bukkit.getWorld(UUID.fromString(parts[0]));
            if (world == null) {
                return null;
            }
            return new Location(
                    world,
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3])
            );
        } catch (IllegalArgumentException | NumberFormatException ex) {
            return null;
        }
    }
}
