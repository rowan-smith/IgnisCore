package dev.rono.igniscore.item.luckdust;

import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.service.IgnisNbtService;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.ConsumableSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;

final class LuckDustBehavior {
    private static final String[] HARMLESS_EFFECTS = {
            "SPEED", "SLOWNESS", "JUMP_BOOST", "REGENERATION", "NIGHT_VISION", "LUCK"
    };
    private static final String[] FORTUNES = {
            "A calm sea lies ahead.", "Trust your pickaxe today.", "Share food with a friend."
    };

    private final IgnisStrategyContext context;
    private final IgnisNbtService nbt;

    LuckDustBehavior(IgnisStrategyContext context) {
        this.context = context;
        this.nbt = context.nbt();
    }

    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        String id = definition.getId();
        String cooldownKey = "ignis:cooldown:" + id;
        long cooldownTicks = StrategySupport.customInt(definition.getCustomData(), "cooldownTicks", 0);
        if (ConsumableSupport.isOnCooldown(nbt, item, cooldownKey, cooldownTicks)) {
            player.sendMessage("<red>Still on cooldown.</red>");
            return;
        }

        IgnisWorld world = player.getWorld();
        IgnisLocation loc = player.getLocation();

        switch (id) {
            case "miners-lunch" -> {
                player.applyPotionEffect("HASTE", 1200, 0);
                player.applyPotionEffect("SATURATION", 60, 1);
                player.sendMessage("<gold>Miner's lunch — haste and saturation!</gold>");
            }
            case "farmers-tea" -> {
                BlockScanSupport.bonemealRadius(world, loc, StrategySupport.customInt(definition.getCustomData(), "cropRadius", 5));
                player.sendMessage("<green>Farmer's tea nourishes nearby crops.</green>");
            }
            case "divers-salt" -> {
                player.applyPotionEffect("WATER_BREATHING", 1800, 0);
                player.sendMessage("<aqua>Diver's salt — water breathing!</aqua>");
                world.playSound(loc, "BLOCK_SAND_PLACE", 0.7f, 1.0f);
            }
            case "cartographers-espresso" -> {
                player.applyPotionEffect("SPEED", 600, 1);
                nbt.setItemString(item, "ignis:compass_heading", "ruins");
                player.sendMessage("<gold>Espresso points you toward distant ruins.</gold>");
                TheatricsSupport.pulseRing(world, loc, 4.0, "END_ROD");
            }
            case "ghost-peppermint" -> {
                nbt.setItemBoolean(item, "ignis:phantom_ignore", true);
                player.sendMessage("<gray>Ghost peppermint — phantoms ignore you until you sleep.</gray>");
            }
            case "heavy-coat-tonic" -> {
                player.applyPotionEffect("SLOWNESS", 900, 0);
                player.applyPotionEffect("RESISTANCE", 900, 1);
                player.sendMessage("<blue>Heavy coat tonic — slow but tough.</blue>");
            }
            case "honey-throat-coat" -> {
                player.applyPotionEffect("REGENERATION", 100, 1);
                nbt.setItemBoolean(item, "ignis:sweet_mark", true);
                player.sendMessage("<yellow>Honey coat soothes poison and sweetens your scent.</yellow>");
            }
            case "chorus-bite" -> {
                double dx = (Math.random() - 0.5) * 16;
                double dz = (Math.random() - 0.5) * 16;
                IgnisLocation dest = loc.add(dx, 0, dz);
                world.spawnParticle(loc, "PORTAL", 20, 0.5, 1, 0.5, 0.2);
                world.spawnParticle(dest, "REVERSE_PORTAL", 20, 0.5, 1, 0.5, 0.2);
                player.sendMessage("<light_purple>Chorus bite warps you sideways.</light_purple>");
            }
            case "glow-berry-shot" -> {
                player.applyPotionEffect("NIGHT_VISION", 2400, 0);
                player.applyPotionEffect("GLOWING", 2400, 0);
                player.sendMessage("<dark_aqua>Glow berry shot lights your night.</dark_aqua>");
            }
            case "bricklayers-broth" -> {
                player.applyPotionEffect("HASTE", 1800, 1);
                nbt.setItemBoolean(item, "ignis:fast_place", true);
                player.sendMessage("<gray>Bricklayer's broth — swift placement!</gray>");
            }
            case "luck-dust" -> {
                if (clickedBlock == null) {
                    player.sendMessage("<gray>Sprinkle luck dust on the ground near a chest.</gray>");
                    return;
                }
                nbt.setItemBoolean(item, "ignis:luck_dust_active", true);
                TheatricsSupport.sparkle(world, clickedBlock.getLocation(), "HAPPY_VILLAGER", 8);
                player.sendMessage("<gold>Luck dust sprinkled — next chest bonus roll!</gold>");
                ConsumableSupport.consumeOne(item);
                return;
            }
            case "antidote-swab" -> {
                player.applyPotionEffect("REGENERATION", 60, 0);
                player.sendMessage("<green>Antidote swab clears ailments.</green>");
            }
            case "unlabeled-potion" -> {
                String known = nbt.getItemString(item, "ignis:identified_effect");
                String effect = known != null && !known.isBlank()
                        ? known
                        : HARMLESS_EFFECTS[(int) (Math.random() * HARMLESS_EFFECTS.length)];
                if (known == null || known.isBlank()) {
                    nbt.setItemString(item, "ignis:identified_effect", effect);
                }
                player.applyPotionEffect(effect, 600, 0);
                player.sendMessage("<light_purple>Unlabeled potion: <white>" + effect + "</white></light_purple>");
            }
            default -> {
                String effect = StrategySupport.customString(definition.getCustomData(), "potionEffect", "REGENERATION");
                int duration = StrategySupport.customInt(definition.getCustomData(), "effectDuration", 200);
                int amp = StrategySupport.customInt(definition.getCustomData(), "effectAmplifier", 0);
                player.applyPotionEffect(effect, duration, amp);
            }
        }

        ConsumableSupport.markUsed(nbt, item, cooldownKey);
        ConsumableSupport.consumeOne(item);
        world.playSound(loc, "ENTITY_GENERIC_DRINK", 0.8f, 1.0f);
    }
}
