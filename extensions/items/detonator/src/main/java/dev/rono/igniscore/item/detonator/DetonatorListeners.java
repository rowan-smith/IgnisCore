package dev.rono.igniscore.item.detonator;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.api.event.ItemClickEvent;
import dev.rono.igniscore.api.event.OnItemClickListener;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
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
            case "assign" -> {
                if (event.clickedBlock() == null) {
                    return;
                }
                IgnisLocation location = event.clickedBlock().getLocation();
                String blockType = IgnisCoreAPI.getPlacedBlockType(Locations.toBlock(location));
                if (!isTargetBlock(event.definition(), blockType)) {
                    event.player().getWorld().playSound(event.player().getLocation(), "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.5f);
                    event.player().sendMessage("§cThat block cannot be linked to this detonator.");
                    return;
                }
                String encoded = encodeLocation(location);
                List<String> linkedBombs = linkStorage.readLinkedBombs(event.item());
                if (linkedBombs.contains(encoded)) {
                    event.player().getWorld().playSound(event.player().getLocation(), "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.8f);
                    event.player().sendMessage("§eThis signal charge is already linked.");
                    return;
                }
                int maxLinks = StrategySupport.customInt(event.definition().getCustomData(), "max_links", 16);
                if (linkedBombs.size() >= maxLinks) {
                    event.player().getWorld().playSound(event.player().getLocation(), "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.4f);
                    event.player().sendMessage("§cThis detonator is full. Detonate or clear links first.");
                    return;
                }
                linkedBombs.add(encoded);
                linkStorage.writeLinkedBombs(event.item(), linkedBombs);
                IgnisLocation center = Locations.toCenter(location);
                event.player().getWorld().playSound(center, "BLOCK_BEACON_POWER_SELECT", 1.0f, 1.6f);
                event.player().getWorld().spawnParticle(center, "HAPPY_VILLAGER", 8, 0.25, 0.25, 0.25, 0.0);
                event.player().sendMessage("§aSignal charge linked. §7(" + linkedBombs.size() + "/" + maxLinks + ")");
            }
            case "detonate" -> {
                List<String> linkedBombs = linkStorage.readLinkedBombs(event.item());
                if (linkedBombs.isEmpty()) {
                    event.player().getWorld().playSound(event.player().getLocation(), "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.5f);
                    event.player().sendMessage("§cNo linked charges. Left-click a signal charge to assign one.");
                    return;
                }
                int triggered = 0;
                Iterator<String> iterator = linkedBombs.iterator();
                while (iterator.hasNext()) {
                    String encoded = iterator.next();
                    IgnisLocation bombLocation = decodeLocation(encoded);
                    if (bombLocation == null) {
                        iterator.remove();
                        continue;
                    }
                    String bombType = IgnisCoreAPI.getPlacedBlockType(Locations.toBlock(bombLocation));
                    if (!isTargetBlock(event.definition(), bombType)) {
                        iterator.remove();
                        continue;
                    }
                    IgnisCoreAPI.ignitePlacedBlock(Locations.toBlock(bombLocation), event.player());
                    iterator.remove();
                    triggered++;
                }
                linkStorage.writeLinkedBombs(event.item(), linkedBombs);
                if (triggered > 0) {
                    event.player().getWorld().playSound(event.player().getLocation(), "BLOCK_BEACON_ACTIVATE", 1.0f, 1.2f);
                    event.player().sendMessage("§cDetonating §f" + triggered + "§c linked charge" + (triggered == 1 ? "" : "s") + ".");
                    return;
                }
                event.player().getWorld().playSound(event.player().getLocation(), "BLOCK_NOTE_BLOCK_BASS", 0.8f, 0.5f);
                event.player().sendMessage("§cNo linked charges remain in range.");
            }
            default -> { }
        }
    }
}
