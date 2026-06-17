package dev.rono.igniscore.item.detonator;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.igniscore.api.util.Locations;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

final class DetonatorListeners implements OnItemClickListener {
    private final DetonatorLinkStorage linkStorage;

    DetonatorListeners(DetonatorLinkStorage linkStorage) {
        this.linkStorage = linkStorage;
    }

    void assignBomb(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        if (clickedBlock == null) {
            return;
        }

        IgnisLocation location = clickedBlock.getLocation();
        String blockType = IgnisCoreAPI.getPlacedBlockType(Locations.toBlock(location));
        if (!isTargetBlock(definition, blockType)) {
            player.getWorld().playSound(player.getLocation(), "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.5f);
            player.sendMessage("§cThat block cannot be linked to this detonator.");
            return;
        }

        String encoded = encodeLocation(location);
        List<String> linkedBombs = linkStorage.readLinkedBombs(item);
        if (linkedBombs.contains(encoded)) {
            player.getWorld().playSound(player.getLocation(), "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.8f);
            player.sendMessage("§eThis signal charge is already linked.");
            return;
        }

        int maxLinks = StrategySupport.customInt(definition.getCustomData(), "max_links", 16);
        if (linkedBombs.size() >= maxLinks) {
            player.getWorld().playSound(player.getLocation(), "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.4f);
            player.sendMessage("§cThis detonator is full. Detonate or clear links first.");
            return;
        }

        linkedBombs.add(encoded);
        linkStorage.writeLinkedBombs(item, linkedBombs);

        IgnisLocation center = Locations.toCenter(location);
        player.getWorld().playSound(center, "BLOCK_BEACON_POWER_SELECT", 1.0f, 1.6f);
        player.getWorld().spawnParticle(center, "HAPPY_VILLAGER", 8, 0.25, 0.25, 0.25, 0.0);
        player.sendMessage("§aSignal charge linked. §7(" + linkedBombs.size() + "/" + maxLinks + ")");
    }

    void detonateLinkedBombs(IgnisPlayer player, ItemDefinition definition, IgnisItem item) {
        List<String> linkedBombs = linkStorage.readLinkedBombs(item);
        if (linkedBombs.isEmpty()) {
            player.getWorld().playSound(player.getLocation(), "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.5f);
            player.sendMessage("§cNo linked charges. Left-click a signal charge to assign one.");
            return;
        }

        int triggered = 0;
        Iterator<String> iterator = linkedBombs.iterator();
        while (iterator.hasNext()) {
            String encoded = iterator.next();
            IgnisLocation location = decodeLocation(encoded);
            if (location == null) {
                iterator.remove();
                continue;
            }

            String blockType = IgnisCoreAPI.getPlacedBlockType(Locations.toBlock(location));
            if (!isTargetBlock(definition, blockType)) {
                iterator.remove();
                continue;
            }

            IgnisCoreAPI.ignitePlacedBlock(Locations.toBlock(location), player);
            iterator.remove();
            triggered++;
        }

        linkStorage.writeLinkedBombs(item, linkedBombs);

        if (triggered > 0) {
            player.getWorld().playSound(player.getLocation(), "BLOCK_BEACON_ACTIVATE", 1.0f, 1.2f);
            player.sendMessage("§cDetonating §f" + triggered + "§c linked charge" + (triggered == 1 ? "" : "s") + ".");
            return;
        }

        player.getWorld().playSound(player.getLocation(), "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.5f);
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

    private String encodeLocation(IgnisLocation location) {
        UUID worldId = location.worldId();
        String worldName = location.worldName() == null ? "world" : location.worldName();
        UUID resolvedId = worldId != null ? worldId : UUID.nameUUIDFromBytes(worldName.getBytes());
        return resolvedId
                + ":" + worldName
                + ":" + (int) Math.floor(location.x())
                + ":" + (int) Math.floor(location.y())
                + ":" + (int) Math.floor(location.z());
    }

    private IgnisLocation decodeLocation(String encoded) {
        String[] parts = encoded.split(":");
        if (parts.length != 5) {
            return null;
        }

        try {
            UUID worldId = UUID.fromString(parts[0]);
            return new IgnisLocation(
                    worldId,
                    parts[1],
                    Integer.parseInt(parts[2]),
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4]),
                    0f,
                    0f);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    @Override
    public void onItemClick(ItemClickEvent event) {
        switch (event.actionToken()) {
                case "assign" -> assignBomb(event.player(), event.definition(), event.item(), event.clickedBlock());
                case "detonate" -> detonateLinkedBombs(event.player(), event.definition(), event.item());
                default -> { }
            }
    }
}
