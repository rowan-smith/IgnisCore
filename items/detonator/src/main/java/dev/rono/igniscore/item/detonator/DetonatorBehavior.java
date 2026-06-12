package dev.rono.igniscore.item.detonator;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

final class DetonatorBehavior {
    private final DetonatorLinkStorage linkStorage;

    DetonatorBehavior(DetonatorLinkStorage linkStorage) {
        this.linkStorage = linkStorage;
    }

    void assignBomb(Player player, ItemDefinition definition, ItemStack item, Block clickedBlock) {
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
        List<String> linkedBombs = linkStorage.readLinkedBombs(item);
        if (linkedBombs.contains(encoded)) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.8f);
            player.sendMessage("§eThis signal charge is already linked.");
            return;
        }

        int maxLinks = StrategySupport.customInt(definition.getCustomData(), "max_links", 16);
        if (linkedBombs.size() >= maxLinks) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.4f);
            player.sendMessage("§cThis detonator is full. Detonate or clear links first.");
            return;
        }

        linkedBombs.add(encoded);
        linkStorage.writeLinkedBombs(item, linkedBombs);

        Location center = Locations.toCenter(location);
        center.getWorld().playSound(center, Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.6f);
        center.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, center, 8, 0.25, 0.25, 0.25, 0.0);
        player.sendMessage("§aSignal charge linked. §7(" + linkedBombs.size() + "/" + maxLinks + ")");
    }

    void detonateLinkedBombs(Player player, ItemDefinition definition, ItemStack item) {
        List<String> linkedBombs = linkStorage.readLinkedBombs(item);
        if (linkedBombs.isEmpty()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
            player.sendMessage("§cNo linked charges. Left-click a signal charge to assign one.");
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

        linkStorage.writeLinkedBombs(item, linkedBombs);

        if (triggered > 0) {
            player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.2f);
            player.sendMessage("§cDetonating §f" + triggered + "§c linked charge" + (triggered == 1 ? "" : "s") + ".");
            return;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.5f);
        player.sendMessage("§cNo linked charges remain in range.");
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

        return "signal-charge".equalsIgnoreCase(blockType);
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
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
