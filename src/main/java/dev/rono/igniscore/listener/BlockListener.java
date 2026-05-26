package dev.rono.igniscore.listener;

import dev.rono.igniscore.api.IgnisCoreAPI;
import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.model.BlockDefinition;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlockListener implements Listener {
    private static final Material CUSTOM_BLOCK_BACKING_MATERIAL = Material.BARRIER;
    private static final int CUSTOM_BLOCK_BREAK_TICKS = 35;
    private static final String ACTION_BREAK = "break";
    private static final String ACTION_IGNITE = "ignite";

    private final JavaPlugin plugin;
    private final BlockManager manager;
    private final NamespacedKey blockTypeKey;
    private final Map<UUID, MiningSession> miningSessions = new ConcurrentHashMap<>();

    public BlockListener(JavaPlugin plugin, BlockManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.blockTypeKey = new NamespacedKey(plugin, "block_type");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent event) {
        if (event.getAction() == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK || event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                String typeId = manager.getPlacedBlockType(clickedBlock.getLocation());
                if (typeId != null) {
                    BlockDefinition definition = manager.getBlockTypes().get(typeId);
                    if (definition == null) return;

                    ItemStack heldItem = event.getItem();
                    String action = getConfiguredAction(definition, event.getAction(), heldItem);
                    if (ACTION_IGNITE.equals(action)) {
                        event.setCancelled(true);
                        igniteCustomBlock(clickedBlock, definition, event.getPlayer(), heldItem);
                        return;
                    }

                    if (ACTION_BREAK.equals(action)) {
                        event.setCancelled(true);
                        startCustomBlockBreak(event.getPlayer(), clickedBlock, definition);
                        return;
                    }
                }
            }
        }

        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;
        
        ItemStack item = event.getItem();
        if (item == null || item.getType() == Material.AIR) return;

        String typeId = IgnisCoreAPI.getNbtService().readItem(item, nbt -> nbt.getString("ignis:block_id"));
        
        if (typeId == null || typeId.isEmpty()) {
            // Fallback to PersistentDataContainer
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                typeId = meta.getPersistentDataContainer().get(blockTypeKey, PersistentDataType.STRING);
                if (typeId == null) {
                    typeId = meta.getPersistentDataContainer().get(new NamespacedKey("igniscore", "tnt_type"), PersistentDataType.STRING);
                }
            }
        }
        
        if (typeId != null && manager.getBlockTypes().containsKey(typeId)) {
            manager.getPlugin().debug("Attempting to place custom block: " + typeId);
            org.bukkit.block.Block clickedBlock = event.getClickedBlock();
            if (clickedBlock == null) return;

            org.bukkit.block.Block targetBlock = clickedBlock.getRelative(event.getBlockFace());
            
            // Basic placement checks
            if (!targetBlock.getType().isAir() && !targetBlock.isReplaceable()) {
                manager.getPlugin().debug("Target block is not air or replaceable: " + targetBlock.getType());
                return;
            }
            
            // Cancel event and place block
            event.setCancelled(true);
            
            targetBlock.setType(CUSTOM_BLOCK_BACKING_MATERIAL);
            manager.registerPlacedBlock(targetBlock.getLocation(), typeId);
            event.getPlayer().swingMainHand();
            manager.getPlugin().debug("Successfully placed " + typeId + " at " + targetBlock.getLocation().toVector());
            
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                item.setAmount(item.getAmount() - 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        
        // Try to get block id from NBT
        String typeId = IgnisCoreAPI.getNbtService().readItem(item, nbt -> nbt.getString("ignis:block_id"));
        
        if (typeId == null || typeId.isEmpty()) {
            // Fallback to PersistentDataContainer for backward compatibility
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                typeId = meta.getPersistentDataContainer().get(blockTypeKey, PersistentDataType.STRING);
                if (typeId == null) {
                    typeId = meta.getPersistentDataContainer().get(new NamespacedKey("igniscore", "tnt_type"), PersistentDataType.STRING);
                }
            }
        }
        
        if (typeId != null && manager.getBlockTypes().containsKey(typeId)) {
            manager.registerPlacedBlock(event.getBlock().getLocation(), typeId);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        String typeId = manager.getPlacedBlockType(event.getBlock().getLocation());
        if (typeId != null) {
            event.setCancelled(true);
            event.setDropItems(false);
            BlockDefinition definition = manager.getBlockTypes().get(typeId);
            if (definition != null) {
                startCustomBlockBreak(event.getPlayer(), event.getBlock(), definition);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        String typeId = manager.getPlacedBlockType(event.getBlock().getLocation());
        if (typeId != null) {
            event.setCancelled(true);
            event.setInstaBreak(false);
            BlockDefinition definition = manager.getBlockTypes().get(typeId);
            if (definition != null) {
                startCustomBlockBreak(event.getPlayer(), event.getBlock(), definition);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockDamageAbort(BlockDamageAbortEvent event) {
        MiningSession session = miningSessions.get(event.getPlayer().getUniqueId());
        if (session != null && session.location.equals(event.getBlock().getLocation())) {
            cancelMiningSession(event.getPlayer().getUniqueId());
        }
    }

    private void startCustomBlockBreak(Player player, Block block, BlockDefinition definition) {
        String typeId = definition.getId();
        if (player.getGameMode() == GameMode.CREATIVE) {
            breakCustomBlock(block, definition, false);
            return;
        }

        if (!definition.isBreakable()) {
            return;
        }

        MiningSession existing = miningSessions.get(player.getUniqueId());
        if (existing != null && existing.location.equals(block.getLocation())) {
            return;
        }

        cancelMiningSession(player.getUniqueId());

        Location location = block.getLocation();
        int totalTicks = getBreakTicks(definition, player.getInventory().getItemInMainHand());
        MiningSession session = new MiningSession(location, typeId, player.getEntityId(), totalTicks);
        session.task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isOnline() || !typeId.equals(manager.getPlacedBlockType(location))) {
                cancelMiningSession(player.getUniqueId());
                return;
            }

            session.elapsedTicks++;
            float progress = Math.min(1.0f, session.elapsedTicks / (float) session.totalTicks);
            sendBlockDamage(location, progress, session.sourceEntityId);

            int hitInterval = getInt(definition.getBreakSettings(), "hit_interval_ticks", 6);
            if (hitInterval > 0 && session.elapsedTicks % hitInterval == 0) {
                playSound(location.toCenterLocation(), getString(definition.getBreakSettings(), "hit_sound", "BLOCK_STONE_HIT"), 0.25f, 1.2f);
                spawnConfiguredParticles(location.toCenterLocation(), getList(getMap(definition.getBreakSettings(), "particles"), "hit"),
                        Particle.CRIT, 3, 0.3, 0.3, 0.3, 0.02);
            }

            if (session.elapsedTicks >= session.totalTicks) {
                cancelMiningSession(player.getUniqueId());
                breakCustomBlock(block, definition, true);
            }
        }, 0L, 1L);

        miningSessions.put(player.getUniqueId(), session);
    }

    private int getBreakTicks(BlockDefinition definition, ItemStack tool) {
        int baseTicks = getInt(definition.getBreakSettings(), "ticks", CUSTOM_BLOCK_BREAK_TICKS);
        if (tool == null) return baseTicks;

        Material type = tool.getType();
        String name = type.name();
        Map<String, Object> toolModifiers = getMap(definition.getBreakSettings(), "tool_ticks");
        for (Map.Entry<String, Object> entry : toolModifiers.entrySet()) {
            String suffix = entry.getKey().toUpperCase();
            if (name.endsWith(suffix)) {
                return Math.max(1, asInt(entry.getValue(), baseTicks));
            }
        }

        return baseTicks;
    }

    private void breakCustomBlock(Block block, BlockDefinition definition, boolean dropItem) {
        if (!definition.isBreakable()) {
            return;
        }

        Location center = block.getLocation().toCenterLocation();
        playSound(center, getString(definition.getBreakSettings(), "break_sound", "BLOCK_STONE_BREAK"), 0.8f, 1.0f);
        spawnConfiguredParticles(center, getList(getMap(definition.getBreakSettings(), "particles"), "break"),
                Particle.BLOCK, 24, 0.35, 0.35, 0.35, 0.01);

        if (dropItem) {
            block.getWorld().dropItemNaturally(block.getLocation(), manager.getPlugin().createBlockItem(definition.getId()));
        }
        manager.unregisterPlacedBlock(block.getLocation());
        block.setType(Material.AIR);
    }

    private void igniteCustomBlock(Block block, BlockDefinition definition, Player player, ItemStack ignitionItem) {
        cancelMiningSession(player.getUniqueId());

        Location location = block.getLocation();
        Location center = location.toCenterLocation();
        Map<String, Object> igniteSettings = getMap(definition.getInteractionSettings(), ACTION_IGNITE);
        playSound(center, getString(igniteSettings, "sound", "ITEM_FLINTANDSTEEL_USE"), 1.0f, 1.0f);
        spawnConfiguredParticles(center, getList(igniteSettings, "particles"), Particle.FLAME, 18, 0.35, 0.35, 0.35, 0.03);

        manager.unregisterPlacedBlock(location);
        block.setType(Material.AIR);
        damageIgnitionItem(player, ignitionItem);
        manager.triggerBlock(location, definition.getId(), player);
    }

    private void damageIgnitionItem(Player player, ItemStack item) {
        if (player.getGameMode() == GameMode.CREATIVE || item == null) return;

        if (item.getType() == Material.FIRE_CHARGE) {
            item.setAmount(item.getAmount() - 1);
            return;
        }

        if (item.getItemMeta() instanceof org.bukkit.inventory.meta.Damageable damageable) {
            damageable.setDamage(damageable.getDamage() + 1);
            item.setItemMeta(damageable);
            if (damageable.getDamage() >= item.getType().getMaxDurability()) {
                item.setAmount(item.getAmount() - 1);
            }
        }
    }

    private String getConfiguredAction(BlockDefinition definition, org.bukkit.event.block.Action clickAction, ItemStack item) {
        String clickKey = clickAction == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK ? "left_click" : "right_click";
        Map<String, Object> clickSettings = getMap(definition.getInteractionSettings(), clickKey);
        for (Object materialAction : getList(clickSettings, "material_actions")) {
            if (!(materialAction instanceof Map<?, ?> rawMap)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            String action = getString(map, "action", "");
            if (!action.isEmpty() && matchesConfiguredMaterials(map, item)) {
                return action.toLowerCase();
            }
        }

        String configuredAction = getString(clickSettings, "action", getString(clickSettings, "default_action", ""));

        if (!configuredAction.isEmpty()) {
            if (ACTION_IGNITE.equalsIgnoreCase(configuredAction) && !matchesConfiguredMaterials(clickSettings, item)) {
                return "";
            }
            return configuredAction.toLowerCase();
        }

        if (matchesDefaultIgnitionMaterial(item)) {
            return ACTION_IGNITE;
        }

        return clickAction == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK ? ACTION_BREAK : "";
    }

    private boolean matchesConfiguredMaterials(Map<String, Object> settings, ItemStack item) {
        List<?> materials = getList(settings, "materials");
        if (materials.isEmpty()) {
            return true;
        }
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        String itemType = item.getType().name();
        for (Object material : materials) {
            if (material != null && itemType.equalsIgnoreCase(material.toString())) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesDefaultIgnitionMaterial(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return false;
        Material type = item.getType();
        return type == Material.FLINT_AND_STEEL || type == Material.FIRE_CHARGE || type == Material.FLINT;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> source, String key) {
        if (source == null) return Map.of();
        Object value = source.get(key);
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private List<?> getList(Map<String, Object> source, String key) {
        if (source == null) return List.of();
        Object value = source.get(key);
        if (value instanceof List<?> list) {
            return list;
        }
        return List.of();
    }

    private String getString(Map<String, Object> source, String key, String defaultValue) {
        if (source == null) return defaultValue;
        Object value = source.get(key);
        return value != null ? value.toString() : defaultValue;
    }

    private int getInt(Map<String, Object> source, String key, int defaultValue) {
        if (source == null) return defaultValue;
        return asInt(source.get(key), defaultValue);
    }

    private int asInt(Object value, int defaultValue) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private double getDouble(Map<String, Object> source, String key, double defaultValue) {
        if (source == null) return defaultValue;
        Object value = source.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value != null) {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private void playSound(Location location, String soundName, float volume, float pitch) {
        try {
            location.getWorld().playSound(location, Sound.valueOf(soundName.toUpperCase()), volume, pitch);
        } catch (IllegalArgumentException ignored) {
            manager.getPlugin().debug("Invalid sound in block config: " + soundName);
        }
    }

    private void spawnConfiguredParticles(Location location, List<?> particles, Particle fallbackParticle, int fallbackCount,
                                          double fallbackOffsetX, double fallbackOffsetY, double fallbackOffsetZ, double fallbackSpeed) {
        if (particles.isEmpty()) {
            spawnParticle(location, fallbackParticle, fallbackCount, fallbackOffsetX, fallbackOffsetY, fallbackOffsetZ, fallbackSpeed, Material.STONE);
            return;
        }

        for (Object particleConfig : particles) {
            if (!(particleConfig instanceof Map<?, ?> rawMap)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) rawMap;
            Particle particle;
            try {
                particle = Particle.valueOf(getString(map, "type", fallbackParticle.name()).toUpperCase());
            } catch (IllegalArgumentException ignored) {
                manager.getPlugin().debug("Invalid particle in block config: " + map.get("type"));
                continue;
            }

            int count = getInt(map, "count", fallbackCount);
            double offsetX = getDouble(map, "offset_x", fallbackOffsetX);
            double offsetY = getDouble(map, "offset_y", fallbackOffsetY);
            double offsetZ = getDouble(map, "offset_z", fallbackOffsetZ);
            double speed = getDouble(map, "speed", fallbackSpeed);
            Material blockMaterial = Material.matchMaterial(getString(map, "block", "STONE"));
            spawnParticle(location, particle, count, offsetX, offsetY, offsetZ, speed, blockMaterial != null ? blockMaterial : Material.STONE);
        }
    }

    private void spawnParticle(Location location, Particle particle, int count, double offsetX, double offsetY, double offsetZ,
                               double speed, Material blockMaterial) {
        if (particle == Particle.BLOCK || particle == Particle.BLOCK_CRUMBLE || particle == Particle.FALLING_DUST) {
            location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed, blockMaterial.createBlockData());
        } else {
            location.getWorld().spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, speed);
        }
    }

    private void cancelMiningSession(UUID playerId) {
        MiningSession session = miningSessions.remove(playerId);
        if (session == null) return;
        if (session.task != null) {
            session.task.cancel();
        }
        sendBlockDamage(session.location, 0.0f, session.sourceEntityId);
    }

    private void sendBlockDamage(Location location, float progress, int sourceEntityId) {
        for (Player viewer : location.getWorld().getPlayers()) {
            viewer.sendBlockDamage(location, progress, sourceEntityId);
        }
    }

    private static class MiningSession {
        private final Location location;
        private final String typeId;
        private final int sourceEntityId;
        private final int totalTicks;
        private int elapsedTicks;
        private BukkitTask task;

        private MiningSession(Location location, String typeId, int sourceEntityId, int totalTicks) {
            this.location = location;
            this.typeId = typeId;
            this.sourceEntityId = sourceEntityId;
            this.totalTicks = totalTicks;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onNotePlay(org.bukkit.event.block.NotePlayEvent event) {
        if (manager.getPlacedBlockType(event.getBlock().getLocation()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTNTPrime(org.bukkit.event.block.TNTPrimeEvent event) {
        String typeId = manager.getPlacedBlockType(event.getBlock().getLocation());
        if (typeId != null) {
            event.setCancelled(true);
            event.getBlock().setType(Material.AIR);
            manager.unregisterPlacedBlock(event.getBlock().getLocation());
            manager.triggerBlock(event.getBlock().getLocation(), typeId, event);
        }
    }
}
