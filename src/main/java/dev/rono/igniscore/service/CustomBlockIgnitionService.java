package dev.rono.igniscore.service;

import dev.rono.igniscore.manager.BlockManager;
import dev.rono.igniscore.model.BlockDefinition;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

import static dev.rono.igniscore.util.ConfigValueReader.getList;
import static dev.rono.igniscore.util.ConfigValueReader.getMap;
import static dev.rono.igniscore.util.ConfigValueReader.getString;

public class CustomBlockIgnitionService {
    private static final String ACTION_IGNITE = "ignite";

    private final BlockManager blockManager;
    private final CustomBlockBreakService breakService;
    private final ConfiguredEffectService effectService;

    public CustomBlockIgnitionService(BlockManager blockManager, CustomBlockBreakService breakService,
                                      ConfiguredEffectService effectService) {
        this.blockManager = blockManager;
        this.breakService = breakService;
        this.effectService = effectService;
    }

    public void ignite(Block block, BlockDefinition definition, Player player, ItemStack ignitionItem) {
        breakService.cancel(player.getUniqueId());

        Location location = block.getLocation();
        Location center = location.toCenterLocation();
        Map<String, Object> igniteSettings = getMap(definition.getInteractionSettings(), ACTION_IGNITE);
        effectService.playSound(center, getString(igniteSettings, "sound", "ITEM_FLINTANDSTEEL_USE"), 1.0f, 1.0f);
        effectService.spawnConfiguredParticles(center, getList(igniteSettings, "particles"), Particle.FLAME,
                18, 0.35, 0.35, 0.35, 0.03);

        blockManager.unregisterPlacedBlock(location);
        block.setType(Material.AIR);
        damageIgnitionItem(player, ignitionItem);
        blockManager.triggerBlock(location, definition.getId(), player);
    }

    private void damageIgnitionItem(Player player, ItemStack item) {
        if (player.getGameMode() == GameMode.CREATIVE || item == null) {
            return;
        }

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
}
