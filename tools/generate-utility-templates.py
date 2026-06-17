#!/usr/bin/env python3
"""Generate utility behavior Java templates for IgnisCore extensions."""

from pathlib import Path

OUT = Path(__file__).resolve().parent / "utility-behaviors"

HEADER_FUSE = """package dev.rono.igniscore.block.{{PACKAGE}};

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.ExplosionSupport;
import dev.rono.extensions.shared.strategy.ExplosionVariantsSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.util.PlacedMetaSupport;
"""

HEADER_PLACED = """package dev.rono.igniscore.block.{{PACKAGE}};

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.BlockScanSupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.PlacedTickSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;
"""

HEADER_INTERACT = """package dev.rono.igniscore.block.{{PACKAGE}};

import dev.rono.igniscore.api.CustomBlockAction;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisLocation;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.port.IgnisWorld;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategySupport;
import dev.rono.extensions.shared.strategy.EntityUtilSupport;
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;
import dev.rono.igniscore.api.util.PlacedMetaSupport;
"""

HEADER_ITEM = """package dev.rono.igniscore.item.{{PACKAGE}};

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
import dev.rono.extensions.shared.strategy.TheatricsSupport;
import dev.rono.igniscore.api.util.Locations;
"""

CLASS = """
final class {{CLASS}}Behavior {
    private final IgnisStrategyContext context;
"""

CLASS_NBT = """
final class {{CLASS}}Behavior {
    private final IgnisStrategyContext context;
    private final IgnisNbtService nbtService;
"""

CTOR = """
    {{CLASS}}Behavior(IgnisStrategyContext context) {
        this.context = context;
    }
"""

CTOR_NBT = """
    {{CLASS}}Behavior(IgnisStrategyContext context) {
        this.context = context;
        this.nbtService = context.getNbtService();
    }
"""

ITEM_FOOTER = """
}
"""

WORLD_AT = """
    private IgnisWorld worldAt(IgnisLocation location) {
        return context.getExtensionSupport().resolveWorld(location);
    }
}
"""

FUSE_TICK_TRIGGER = """
    void onTick(RuntimeBlockInstance instance) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        int fuse = ExplosionSupport.fuseTicks(instance, 80);
        int elapsed = ExplosionSupport.elapsedFuseTicks(instance, 80);
        int interval = StrategySupport.customInt(def, "tickInterval", 5);
        if (elapsed % interval != 0) {
            return;
        }
        {tick_body}
    }

    void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        BlockDefinition def = instance.getDefinition();
        IgnisLocation loc = Locations.toCenter(instance.getLocation());
        IgnisWorld world = worldAt(loc);
        {trigger_body}
    }
"""

PLACED_TICK = """
    void onPlaced(BlockDefinition definition, IgnisLocation location) {
        long period = StrategySupport.customInt(definition, "tickPeriod", 20);
        PlacedTickSupport.start(context, location, period, () -> tick(definition, location));
        IgnisLocation center = Locations.toCenter(location);
        TheatricsSupport.chime(worldAt(center), center, 1.0f);
    }

    void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
        PlacedTickSupport.stop(location);
    }

    private void tick(BlockDefinition definition, IgnisLocation location) {
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        {tick_body}
    }
"""

INTERACT = """
    void onPlacedInteract(BlockDefinition definition,
                          IgnisLocation location,
                          IgnisPlayer player,
                          dev.rono.igniscore.api.port.IgnisInteraction interaction,
                          IgnisItem heldItem,
                          CustomBlockAction action) {
        if (action != CustomBlockAction.OPEN) {
            return;
        }
        IgnisWorld world = worldAt(location);
        IgnisLocation center = Locations.toCenter(location);
        {body}
    }
"""

ITEM_USE = """
    void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
        IgnisWorld world = player.getWorld();
        IgnisLocation loc = player.getEyeLocation();
        {body}
    }
"""


def write(kind: str, content: str) -> None:
    path = OUT / f"{kind}.java.template"
    path.write_text(content, encoding="utf-8")


def fuse(kind: str, tick_body: str, trigger_body: str) -> None:
    body = FUSE_TICK_TRIGGER.replace("{tick_body}", tick_body).replace("{trigger_body}", trigger_body)
    write(kind, HEADER_FUSE + CLASS + CTOR + body + WORLD_AT)


def placed(kind: str, tick_body: str, extra: str = "") -> None:
    body = PLACED_TICK.replace("{tick_body}", tick_body)
    write(kind, HEADER_PLACED + CLASS + CTOR + body + extra + WORLD_AT)


def interact(kind: str, body: str, extra: str = "") -> None:
    write(kind, HEADER_INTERACT + CLASS + CTOR + INTERACT.replace("{body}", body) + extra + WORLD_AT)


def item(kind: str, body: str, extra: str = "") -> None:
    write(kind, HEADER_ITEM + CLASS_NBT + CTOR_NBT + ITEM_USE.replace("{body}", body) + extra + ITEM_FOOTER)


# --- BLOCK explosive/fuse ---
fuse("splitter",
     """double spread = StrategySupport.customDouble(def, "splitOffset", 2.5);
        TheatricsSupport.pulseRing(world, loc, spread * 0.5, "SMOKE");
        TheatricsSupport.chime(world, loc, 0.8f + elapsed / (float) fuse);""",
     """float power = ExplosionSupport.resolvePower(def, 4.0);
        double offset = StrategySupport.customDouble(def, "splitOffset", 2.5);
        ExplosionVariantsSupport.cardinalSplit(world, loc, power, offset);""")

fuse("ricochet",
     """world.spawnParticle(loc, "CRIT", 4, 0.2, 0.1, 0.2, 0.02);
        if (elapsed % 10 == 0) {
            world.playSound(loc, "ENTITY_FIREWORK_ROCKET_BLAST_FAR", 0.6f, 1.0f + elapsed * 0.01f);
        }""",
     """float power = ExplosionSupport.resolvePower(def, 3.0);
        int bounces = StrategySupport.customInt(def, "bounces", 4);
        double step = StrategySupport.customDouble(def, "step", 2.5);
        float yaw = ExplosionVariantsSupport.resolveYaw(world, instance.getLocation(), triggerContext, context);
        ExplosionVariantsSupport.ricochetRay(world, loc, yaw, bounces, step, power);""")

fuse("cascade",
     """world.spawnParticle(loc, "LAVA", 2, 0.3, 0.2, 0.3, 0.01);""",
     """float power = ExplosionSupport.resolvePower(def, 3.5);
        int waves = StrategySupport.customInt(def, "cascadeWaves", 4);
        int delay = StrategySupport.customInt(def, "cascadeDelay", 6);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
        ExplosionSupport.createExplosion(world, loc, def, power, false);
        for (int i = 1; i <= waves; i++) {
            final int wave = i;
            context.getScheduler().runLater(loc, () -> {
                IgnisLocation ring = loc.add(wave * 1.5, 0, 0);
                world.spawnParticle(ring, "EXPLOSION", 3, 0.4, 0.2, 0.4, 0.02);
                ExplosionSupport.createExplosion(world, ring, power * 0.55f, false, true);
            }, delay * (long) i);
        }""")

fuse("powder_trail",
     """double trailStep = StrategySupport.customDouble(def, "trailStep", 0.6);
        IgnisLocation trail = loc.add(0, -trailStep * (elapsed / (double) interval), 0);
        world.spawnParticle(trail, "CAMPFIRE_COSY_SMOKE", 3, 0.15, 0.05, 0.15, 0.01);
        if (elapsed % 15 == 0) {
            world.playSound(loc, "BLOCK_SAND_PLACE", 0.5f, 1.4f);
        }""",
     """world.playSound(loc, "ENTITY_TNT_PRIMED", 1.0f, 0.8f);
        TheatricsSupport.sparkle(world, loc, "FLAME", 24);
        ExplosionSupport.createExplosion(world, loc, def, 4.0, StrategySupport.customBoolean(def, "fire", false));""")

fuse("pause_fuse",
     """int pauseAt = StrategySupport.customInt(def, "pauseAtElapsed", fuse / 2);
        if (elapsed == pauseAt) {
            TheatricsSupport.sparkle(world, loc, "END_ROD", 16);
            world.playSound(loc, "BLOCK_NOTE_BLOCK_BELL", 1.0f, 1.2f);
        } else if (elapsed > pauseAt && elapsed < pauseAt + StrategySupport.customInt(def, "pauseDuration", 20)) {
            TheatricsSupport.pulseRing(world, loc, 1.2, "REVERSE_PORTAL");
        }""",
     """world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.2f, 0.9f);
        ExplosionSupport.createExplosion(world, loc, def, 4.5, false);""")

fuse("accelerating_fuse",
     """float pitch = 0.6f + (elapsed / (float) Math.max(1, fuse)) * 1.4f;
        int particles = 2 + elapsed / Math.max(1, interval);
        world.spawnParticle(loc, "SMOKE", particles, 0.25, 0.15, 0.25, 0.03);
        if (elapsed % 8 == 0) {
            world.playSound(loc, "BLOCK_NOTE_BLOCK_HAT", 0.7f, pitch);
        }""",
     """world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.5f, 1.1f);
        TheatricsSupport.sparkle(world, loc, "EXPLOSION", 30);
        ExplosionSupport.createExplosion(world, loc, def, 5.0, false);""")

fuse("echo_fuse",
     """if (elapsed % 12 == 0) {
            TheatricsSupport.chime(world, loc, 0.7f + (elapsed % 24) * 0.02f);
            world.spawnParticle(loc, "NOTE", 2, 0.2, 0.3, 0.2, 0.01);
        }""",
     """float power = ExplosionSupport.resolvePower(def, 4.0);
        int echoes = StrategySupport.customInt(def, "echoBursts", 3);
        int delay = StrategySupport.customInt(def, "echoDelay", 10);
        world.playSound(loc, "ENTITY_GENERIC_EXPLODE", 1.0f, 1.0f);
        ExplosionSupport.createExplosion(world, loc, def, power, false);
        for (int i = 1; i <= echoes; i++) {
            final int echo = i;
            context.getScheduler().runLater(loc, () -> {
                world.playSound(loc, "ENTITY_FIREWORK_ROCKET_BLAST", 0.9f, 0.8f + echo * 0.1f);
                world.spawnParticle(loc, "CLOUD", 12, 1.0, 0.5, 1.0, 0.04);
                ExplosionSupport.createExplosion(world, loc, power * 0.45f, false, false);
            }, delay * (long) echo);
        }""")

fuse("blink",
     """if (elapsed % StrategySupport.customInt(def, "blinkInterval", 14) == 0) {
            double range = StrategySupport.customDouble(def, "blinkRange", 1.5);
            double angle = Math.random() * Math.PI * 2;
            IgnisLocation blink = loc.add(Math.cos(angle) * range, 0, Math.sin(angle) * range);
            world.spawnParticle(loc, "PORTAL", 8, 0.2, 0.4, 0.2, 0.05);
            world.spawnParticle(blink, "REVERSE_PORTAL", 8, 0.2, 0.4, 0.2, 0.05);
            world.playSound(loc, "ENTITY_ENDERMAN_TELEPORT", 0.5f, 1.4f);
        }""",
     """EntityUtilSupport.teleportRandomHorizontal(world, loc, StrategySupport.customDouble(def, "blinkRadius", 5.0), 2.5);
        ExplosionSupport.createExplosion(world, loc, def, 3.5, false);""")

fuse("swap",
     """if (elapsed % 20 == 0) {
            TheatricsSupport.pulseRing(world, loc, 1.5, "END_ROD");
        }""",
     """double radius = StrategySupport.customDouble(def, "swapRadius", 8.0);
        EntityUtilSupport.swapNearestPlayers(world, loc, radius);
        ExplosionSupport.createExplosion(world, loc, def, 2.5, false);""")

fuse("phase",
     """world.spawnParticle(loc, "SOUL_FIRE_FLAME", 3, 0.3, 0.2, 0.3, 0.01);""",
     """float power = ExplosionSupport.resolvePower(def, 4.0);
        double radius = StrategySupport.customDouble(def, "phaseRadius", 6.0);
        ExplosionVariantsSupport.phaseBurst(world, loc, power, radius);""")

fuse("mirror",
     """double mirrorY = StrategySupport.customDouble(def, "mirrorY", loc.y());
        IgnisLocation mirror = loc.add(0, (mirrorY * 2) - loc.y() - loc.y(), 0);
        world.spawnParticle(mirror, "END_ROD", 2, 0.1, 0.1, 0.1, 0.01);""",
     """float power = ExplosionSupport.resolvePower(def, 4.0);
        double mirrorY = StrategySupport.customDouble(def, "mirrorY", loc.y());
        ExplosionVariantsSupport.mirrorBlast(world, loc, power, mirrorY);""")

fuse("scaffold",
     """if (elapsed % 10 == 0) {
            int height = StrategySupport.customInt(def, "scaffoldHeight", 4);
            for (int y = 0; y < height; y++) {
                IgnisLocation pillar = Locations.toBlock(instance.getLocation()).add(0, y, 0);
                world.setBlockMaterialKey(pillar, "scaffolding");
                world.spawnParticle(pillar.add(0.5, 0.5, 0.5), "CRIT", 1, 0, 0, 0, 0);
            }
        }""",
     """world.playSound(loc, "BLOCK_SCAFFOLDING_BREAK", 1.2f, 0.8f);
        ExplosionSupport.createExplosion(world, loc, def, 3.0, false);""")

fuse("bridge_builder",
     """int length = StrategySupport.customInt(def, "bridgeLength", 6);
        float yaw = PlacedMetaSupport.placementYaw(instance.getLocation(), 0f);
        double dirX = -Math.sin(Math.toRadians(yaw));
        double dirZ = Math.cos(Math.toRadians(yaw));
        int step = elapsed / Math.max(1, interval);
        if (step > 0 && step <= length) {
            IgnisLocation block = Locations.toBlock(instance.getLocation()).add(dirX * step, 0, dirZ * step);
            world.setBlockMaterialKey(block, StrategySupport.customBoolean(def, "oakBridge", true) ? "oak_planks" : "stone");
            world.spawnParticle(block.add(0.5, 0.5, 0.5), "BLOCK", 2, 0.1, 0.1, 0.1, 0.01);
        }""",
     """world.playSound(loc, "BLOCK_WOOD_PLACE", 1.0f, 0.7f);
        ExplosionSupport.createExplosion(world, loc, def, 2.5, false);""")

fuse("last_stand",
     """double radius = StrategySupport.customDouble(def, "stasisRadius", 5.0);
        if (instance.getTicksLeft() < StrategySupport.customInt(def, "lastStandTicks", 30)) {
            EntityUtilSupport.freezeInRadius(world, loc, radius);
            TheatricsSupport.sparkle(world, loc, "TOTEM_OF_UNDYING", 6);
        }""",
     """world.playSound(loc, "ITEM_TOTEM_USE", 1.0f, 0.8f);
        TheatricsSupport.pulseRing(world, loc, 3.0, "EXPLOSION");
        ExplosionSupport.createExplosion(world, loc, def, StrategySupport.customDouble(def, "lastStandPower", 6.0), false);""")

fuse("rift",
     """double pull = StrategySupport.customDouble(def, "riftPull", 0.12);
        for (Object entity : world.getNearbyEntities(loc, 6.0)) {
            IgnisLocation entityLoc = world.getEntityLocation(entity);
            if (entityLoc == null) {
                continue;
            }
            double dx = loc.x() - entityLoc.x();
            double dy = loc.y() - entityLoc.y();
            double dz = loc.z() - entityLoc.z();
            double dist = Math.max(0.2, Math.sqrt(dx * dx + dy * dy + dz * dz));
            world.setEntityVelocity(entity, dx / dist * pull, dy / dist * pull, dz / dist * pull);
        }
        world.spawnParticle(loc, "PORTAL", 10, 0.4, 0.5, 0.4, 0.08);""",
     """double radius = StrategySupport.customDouble(def, "riftRadius", 7.0);
        EntityUtilSupport.teleportRandomHorizontal(world, loc, radius, 3.0);
        world.playSound(loc, "ENTITY_ENDER_DRAGON_GROWL", 0.8f, 1.2f);
        ExplosionSupport.createExplosion(world, loc, def, 4.5, false);""")

# --- BLOCK placed tick ---

CHICKEN_HELPER = """
    private void playerMessageNearby(IgnisWorld world, IgnisLocation center, String message) {
        for (IgnisPlayer player : world.getPlayersNear(center, 6.0)) {
            player.sendActionBar(message);
        }
    }
"""

CHECKPOINT_HELPER = """
    private void nbtCheckpoint(IgnisPlayer player, IgnisLocation center) {
        player.sendActionBar("<gray>" + (int) center.x() + " " + (int) center.y() + " " + (int) center.z() + "</gray>");
    }
"""

TRAIT_HELPER = """
    private String pickTrait(int roll) {
        String[] traits = {"Artisan", "Scout", "Alchemist", "Engineer", "Duelist"};
        return traits[Math.floorMod(roll, traits.length)];
    }
"""

placed("ore_scan",
       """int radius = StrategySupport.customInt(definition, "scanRadius", 12);
          IgnisLocation ore = BlockScanSupport.findNearestOre(world, center, radius);
          if (ore != null) {
              TheatricsSupport.scanBeam(world, center, ore.add(0.5, 0.5, 0.5), "CRIT");
              world.playSound(center, "BLOCK_AMETHYST_BLOCK_CHIME", 0.6f, 1.4f);
          }""")

placed("xp_vacuum",
       """double radius = StrategySupport.customDouble(definition, "vacuumRadius", 6.0);
          double strength = StrategySupport.customDouble(definition, "vacuumStrength", 0.35);
          EntityUtilSupport.pullLoot(world, center, radius, strength);
          TheatricsSupport.sparkle(world, center, "ENCHANT", 4);""")

placed("light_beacon",
       """TheatricsSupport.sparkle(world, center, "END_ROD", StrategySupport.customInt(definition, "lightCount", 8));
          TheatricsSupport.chime(world, center, 1.2f);
          TheatricsSupport.pulseRing(world, center, StrategySupport.customDouble(definition, "beaconRadius", 3.0), "GLOW");""")

placed("crop_accel",
       """int radius = StrategySupport.customInt(definition, "cropRadius", 4);
          BlockScanSupport.bonemealRadius(world, center, radius);
          world.playSound(center, "ITEM_BONE_MEAL_USE", 0.7f, 1.1f);""")

placed("mob_grinder",
       """double radius = StrategySupport.customDouble(definition, "grindRadius", 5.0);
          for (Object entity : world.getNearbyEntities(center, radius)) {
              if (!EntityUtilSupport.isHostile(entity)) {
                  continue;
              }
              world.setEntityVelocity(entity, 0, -0.6, 0);
              world.spawnParticle(world.getEntityLocation(entity), "DAMAGE_INDICATOR", 2, 0.1, 0.1, 0.1, 0.01);
          }
          if (EntityUtilSupport.countHostiles(world, center, radius) > 0) {
              world.playSound(center, "ENTITY_IRON_GOLEM_ATTACK", 0.5f, 1.3f);
          }""")

placed("stasis_field",
       """double radius = StrategySupport.customDouble(definition, "stasisRadius", 4.5);
          EntityUtilSupport.freezeInRadius(world, center, radius);
          TheatricsSupport.pulseRing(world, center, radius * 0.4, "CLOUD");""")

placed("pollinator",
       """int radius = StrategySupport.customInt(definition, "pollinateRadius", 3);
          BlockScanSupport.bonemealRadius(world, center, radius);
          TheatricsSupport.sparkle(world, center, "HAPPY_VILLAGER", 6);
          world.playSound(center, "ENTITY_BEE_POLLINATE", 0.6f, 1.0f);""")

placed("scarecrow",
       """double radius = StrategySupport.customDouble(definition, "scareRadius", 8.0);
          for (Object entity : world.getNearbyEntities(center, radius)) {
              if (!EntityUtilSupport.isHostile(entity)) {
                  continue;
              }
              IgnisLocation entityLoc = world.getEntityLocation(entity);
              if (entityLoc == null) {
                  continue;
              }
              double dx = entityLoc.x() - center.x();
              double dz = entityLoc.z() - center.z();
              double dist = Math.max(0.5, Math.sqrt(dx * dx + dz * dz));
              world.setEntityVelocity(entity, dx / dist * 0.5, 0.1, dz / dist * 0.5);
          }
          world.spawnParticle(center.add(0, 1.5, 0), "BLOCK", 3, 0.2, 0.2, 0.2, 0.01);""")

placed("compost",
       """TheatricsSupport.sparkle(world, center, "COMPOSTER", StrategySupport.customInt(definition, "compostParticles", 5));
          world.playSound(center, "BLOCK_COMPOSTER_FILL", 0.5f, 1.0f);""")

placed("hydroponic",
       """int radius = StrategySupport.customInt(definition, "hydroRadius", 2);
          BlockScanSupport.bonemealRadius(world, center, radius);
          world.spawnParticle(center, "DRIPPING_WATER", 6, radius * 0.4, 0.2, radius * 0.4, 0.01);""")

placed("shepherd",
       """double radius = StrategySupport.customDouble(definition, "herdRadius", 10.0);
          EntityUtilSupport.herdPassives(world, center, radius);
          world.playSound(center, "ENTITY_SHEEP_AMBIENT", 0.4f, 1.1f);""")

placed("milking",
       """if (EntityUtilSupport.countPassives(world, center, 3.0) > 0) {
              TheatricsSupport.sparkle(world, center, "DRIPPING_HONEY", 4);
              world.playSound(center, "ENTITY_COW_MILK", 0.5f, 1.0f);
          }""")

placed("crop_mri",
       """int radius = StrategySupport.customInt(definition, "mriRadius", 6);
          int crops = BlockScanSupport.countCrops(world, center, radius);
          TheatricsSupport.scanBeam(world, center, center.add(0, 2, 0), "HAPPY_VILLAGER");
          if (crops > 0) {
              world.playSound(center, "BLOCK_NOTE_BLOCK_PLING", 0.5f, 1.0f + crops * 0.05f);
          }""")

placed("chicken_coop",
       """TheatricsSupport.sparkle(world, center, "EGG_CRACK", StrategySupport.customInt(definition, "eggParticles", 3));
          world.playSound(center, "ENTITY_CHICKEN_EGG", 0.4f, 1.2f);
          if (StrategySupport.customBoolean(definition, "collectSimulation", true)) {
              playerMessageNearby(world, center, "<gray>Coop collected <yellow>1 egg</yellow>.</gray>");
          }""",
       extra=CHICKEN_HELPER)

placed("auto_sieve",
       """TheatricsSupport.sparkle(world, center, "BLOCK", StrategySupport.customInt(definition, "sieveParticles", 6));
          world.playSound(center, "BLOCK_SAND_BREAK", 0.4f, 1.3f);""")

placed("drying_rack",
       """world.spawnParticle(center, "CAMPFIRE_COSY_SMOKE", StrategySupport.customInt(definition, "smokeCount", 4),
                  0.4, 0.2, 0.4, 0.01);
          world.playSound(center, "BLOCK_WOOL_BREAK", 0.3f, 0.9f);""")

placed("infuser",
       """TheatricsSupport.sparkle(world, center, "ENCHANT", StrategySupport.customInt(definition, "infuseParticles", 8));
          TheatricsSupport.chime(world, center, 1.4f);""")

placed("recycler",
       """TheatricsSupport.sparkle(world, center, "SCRAP", StrategySupport.customInt(definition, "recycleParticles", 5));
          world.playSound(center, "BLOCK_GRINDSTONE_USE", 0.5f, 1.1f);""")

placed("paint_mixer",
       """String[] colors = {"REDSTONE", "WAX_ON", "WAX_OFF", "COMPOSTER"};
          String particle = colors[(int) (System.currentTimeMillis() / 500 % colors.length)];
          TheatricsSupport.sparkle(world, center, particle, 6);""")

placed("brewing_accel",
       """TheatricsSupport.sparkle(world, center, "WITCH", StrategySupport.customInt(definition, "brewParticles", 5));
          world.playSound(center, "BLOCK_BREWING_STAND_BREW", 0.5f, 1.2f);""")

placed("smoker_stack",
       """world.spawnParticle(center.add(0, 1, 0), "CAMPFIRE_SIGNAL_SMOKE",
                  StrategySupport.customInt(definition, "stackSmoke", 6), 0.2, 0.5, 0.2, 0.02);""")

placed("mob_radar",
       """double radius = StrategySupport.customDouble(definition, "radarRadius", 16.0);
          int hostiles = EntityUtilSupport.countHostiles(world, center, radius);
          if (hostiles > 0) {
              TheatricsSupport.pulseRing(world, center, Math.min(radius, 4 + hostiles), "CRIMSON_SPORE");
              world.playSound(center, "BLOCK_NOTE_BLOCK_BASS", 0.6f, 0.5f + hostiles * 0.05f);
          }""")

placed("chunk_loader",
       """TheatricsSupport.pulseRing(world, center, 2.5, "PORTAL");
          world.playSound(center, "BLOCK_BEACON_AMBIENT", 0.3f, 1.5f);""")

placed("proximity_alarm",
       """double radius = StrategySupport.customDouble(definition, "alarmRadius", 12.0);
          if (!world.getPlayersNear(center, radius).isEmpty()) {
              world.playSound(center, "BLOCK_NOTE_BLOCK_BELL", 1.0f, 0.6f);
              TheatricsSupport.sparkle(world, center, "FIREWORKS_SPARK", 8);
          }""")

placed("motion_floodlight",
       """double radius = StrategySupport.customDouble(definition, "motionRadius", 8.0);
          for (IgnisPlayer player : world.getPlayersNear(center, radius)) {
              TheatricsSupport.scanBeam(world, center, player.getLocation(), "END_ROD");
          }""")

placed("moss_creeper",
       """int radius = StrategySupport.customInt(definition, "mossRadius", 3);
          BlockScanSupport.mossifyNearWater(world, center, radius);""")

placed("deoxidizer",
       """int radius = StrategySupport.customInt(definition, "deoxidizeRadius", 4);
          BlockScanSupport.deoxidizeCopper(world, center, radius);
          world.playSound(center, "ITEM_AXE_WAX_ON", 0.5f, 1.0f);""")

placed("honey_centrifuge",
       """TheatricsSupport.sparkle(world, center, "DRIPPING_HONEY", StrategySupport.customInt(definition, "honeyParticles", 10));
          world.playSound(center, "BLOCK_HONEY_BLOCK_SLIDE", 0.5f, 1.0f);""")

placed("kelp_compressor",
       """world.spawnParticle(center, "BUBBLE", StrategySupport.customInt(definition, "kelpBubbles", 6), 0.3, 0.2, 0.3, 0.02);
          world.playSound(center, "BLOCK_WET_GRASS_BREAK", 0.4f, 0.8f);""")

placed("fish_smoker",
       """world.spawnParticle(center, "SMOKE", StrategySupport.customInt(definition, "fishSmoke", 5), 0.3, 0.3, 0.3, 0.02);
          world.playSound(center, "ENTITY_FISH_SWIM", 0.4f, 0.7f);""")

placed("glow_lantern",
       """TheatricsSupport.sparkle(world, center, "GLOW", StrategySupport.customInt(definition, "glowCount", 6));
          TheatricsSupport.chime(world, center, 1.6f);""")

placed("weather_dome",
       """TheatricsSupport.pulseRing(world, center, StrategySupport.customDouble(definition, "domeRadius", 5.0), "CLOUD");
          world.spawnParticle(center.add(0, 3, 0), "RAIN", 8, 2, 0.1, 2, 0.01);""")

placed("item_pedestal",
       """TheatricsSupport.sparkle(world, center.add(0, 0.8, 0), "ENCHANT", 3);
          world.playSound(center, "BLOCK_AMETHYST_BLOCK_CHIME", 0.3f, 1.8f);""")

placed("blueprint",
       """TheatricsSupport.pulseRing(world, center, 2.0, "END_ROD");
          TheatricsSupport.scanBeam(world, center, center.add(0, 2, 0), "VILLAGER_HAPPY");""")

# --- BLOCK interact ---
interact("repair_station",
         """if (heldItem == null || heldItem.isAir()) {
                player.sendMessage("<yellow>Hold a damaged item to repair.</yellow>");
                return;
            }
            int repairAmount = StrategySupport.customInt(definition, "repairAmount", 25);
            player.sendMessage("<green>Repair station restored <white>" + repairAmount + "</white> durability.</green>");
            TheatricsSupport.sparkle(world, center, "ENCHANT", 16);
            world.playSound(center, "BLOCK_ANVIL_USE", 0.8f, 1.0f);""")

interact("waypoint",
         """String name = StrategySupport.customBoolean(definition, "usePlayerName", true)
                ? player.getName()
                : StrategySupport.customInt(definition, "waypointId", 1) + "";
         PlacedMetaSupport.setString(location, name + ":" + center.x() + "," + center.y() + "," + center.z());
         player.sendMessage("<aqua>Waypoint <white>" + name + "</white> saved.</aqua>");
         TheatricsSupport.sparkle(world, center, "END_ROD", 10);
         world.playSound(center, "ENTITY_EXPERIENCE_ORB_PICKUP", 0.8f, 1.2f);""")

interact("checkpoint",
         """nbtCheckpoint(player, center);
         player.sendMessage("<gold>Checkpoint recorded.</gold>");
         TheatricsSupport.pulseRing(world, center, 2.0, "TOTEM_OF_UNDYING");
         world.playSound(center, "UI_TOAST_CHALLENGE_COMPLETE", 0.7f, 1.0f);""",
         extra=CHECKPOINT_HELPER)

interact("secure_trade",
         """player.sendMessage("<gray>Secure trade window ready. Offer items on the table.</gray>");
         TheatricsSupport.sparkle(world, center, "HAPPY_VILLAGER", 8);
         world.playSound(center, "ENTITY_VILLAGER_TRADE", 0.9f, 1.0f);""")

interact("entity_camera",
         """player.sendMessage("<light_purple>Entity camera linked to nearby mobs.</light_purple>");
         double radius = StrategySupport.customDouble(definition, "cameraRadius", 12.0);
         for (Object entity : world.getNearbyEntities(center, radius)) {
             IgnisLocation entityLoc = world.getEntityLocation(entity);
             if (entityLoc != null) {
                 TheatricsSupport.scanBeam(world, center, entityLoc, "END_ROD");
             }
         }
         world.playSound(center, "BLOCK_BEACON_POWER_SELECT", 0.7f, 1.4f);""")

interact("pocket_cache",
         """player.sendMessage("<gray>Pocket cache opened — collecting nearby drops.</gray>");
         double radius = StrategySupport.customDouble(definition, "cacheRadius", 5.0);
         EntityUtilSupport.pullLoot(world, center, radius, 0.4);
         TheatricsSupport.sparkle(world, center, "ITEM_PICKUP", 12);
         world.playSound(center, "ENTITY_ITEM_PICKUP", 0.8f, 1.1f);""")

# --- ITEM ---
item("block_stethoscope",
     """IgnisBlock target = clickedBlock;
        if (target == null) {
            player.sendMessage("<yellow>Aim at a block to listen.</yellow>");
            return;
        }
        IgnisLocation blockLoc = target.getLocation();
        String material = world.getBlockMaterialKey(blockLoc);
        player.sendMessage("<gray>Stethoscope: <white>" + material + "</white></gray>");
        TheatricsSupport.scanBeam(world, loc, Locations.toCenter(blockLoc), "NOTE");
        world.playSound(loc, "BLOCK_NOTE_BLOCK_HARP", 0.7f, 1.3f);""")

item("lock_item",
     """boolean locked = nbtService.getItemBoolean(item, "ignis:locked", false);
        nbtService.setItemBoolean(item, "ignis:locked", !locked);
        player.sendMessage(locked ? "<green>Lock disengaged.</green>" : "<red>Lock engaged.</red>");
        TheatricsSupport.sparkle(world, loc, locked ? "WAX_OFF" : "WAX_ON", 8);
        world.playSound(loc, "BLOCK_IRON_TRAPDOOR_CLOSE", 0.8f, locked ? 1.2f : 0.8f);""")

item("keyring_beacon",
     """int slots = StrategySupport.customInt(definition.getCustomData(), "beaconSlots", 3);
        int index = nbtService.getItemInt(item, "ignis:beacon_index", 0) % Math.max(1, slots);
        String key = "ignis:beacon_" + index;
        nbtService.setItemString(item, key, loc.x() + "," + loc.y() + "," + loc.z());
        nbtService.setItemInt(item, "ignis:beacon_index", index + 1);
        player.sendMessage("<aqua>Beacon slot <white>" + index + "</white> marked.</aqua>");
        TheatricsSupport.pulseRing(world, loc, 2.0, "END_ROD");
        world.playSound(loc, "BLOCK_BEACON_ACTIVATE", 0.7f, 1.0f);""")

item("trait_badge",
     """int roll = nbtService.getItemInt(item, "ignis:trait_roll", 0) + 1;
        nbtService.setItemInt(item, "ignis:trait_roll", roll);
        String trait = StrategySupport.customBoolean(definition.getCustomData(), "randomTrait", true)
                ? pickTrait(roll)
                : "Artisan";
        nbtService.setItemString(item, "ignis:trait", trait);
        player.sendMessage("<light_purple>Badge trait: <white>" + trait + "</white></light_purple>");
        TheatricsSupport.sparkle(world, loc, "TOTEM_OF_UNDYING", 10);
        world.playSound(loc, "ENTITY_PLAYER_LEVELUP", 0.6f, 1.4f);""",
     extra=TRAIT_HELPER)

item("player_chronicle",
     """int page = nbtService.getItemInt(item, "ignis:chronicle_page", 0) + 1;
        nbtService.setItemInt(item, "ignis:chronicle_page", page);
        String entry = player.getName() + " @ " + (int) loc.x() + "," + (int) loc.y() + "," + (int) loc.z();
        nbtService.setItemString(item, "ignis:chronicle_" + page, entry);
        player.sendMessage("<gold>Chronicle page <white>" + page + "</white> written.</gold>");
        world.playSound(loc, "ITEM_BOOK_PAGE_TURN", 0.8f, 1.0f);
        TheatricsSupport.sparkle(world, loc, "ENCHANT", 6);""")

item("ore_xray_goggles",
     """int radius = StrategySupport.customInt(definition.getCustomData(), "xrayRadius", 14);
        IgnisLocation ore = BlockScanSupport.findNearestOre(world, loc, radius);
        if (ore == null) {
            player.sendMessage("<gray>No ore signature detected.</gray>");
            return;
        }
        player.sendMessage("<green>Ore ping toward <white>" + (int) ore.x() + " " + (int) ore.y() + " " + (int) ore.z() + "</white></green>");
        TheatricsSupport.scanBeam(world, loc, ore.add(0.5, 0.5, 0.5), "CRIT");
        world.playSound(loc, "BLOCK_AMETHYST_BLOCK_RESONATE", 0.7f, 1.5f);""")

item("structure_compass",
     """String heading = nbtService.getItemString(item, "ignis:compass_heading");
        if (heading == null || heading.isBlank()) {
            heading = "north";
            nbtService.setItemString(item, "ignis:compass_heading", heading);
        }
        float yaw = switch (heading.toLowerCase()) {
            case "east" -> 90f;
            case "south" -> 180f;
            case "west" -> 270f;
            default -> 0f;
        };
        player.sendMessage("<aqua>Structure compass bearing: <white>" + heading + "</white> (" + (int) yaw + "°)</aqua>");
        TheatricsSupport.pulseRing(world, loc, 3.0, "END_ROD");
        world.playSound(loc, "ITEM_LODESTONE_COMPASS_LOCK", 0.8f, 1.0f);""")

item("chunk_grid_overlay",
     """int chunkX = (int) Math.floor(loc.x()) >> 4;
        int chunkZ = (int) Math.floor(loc.z()) >> 4;
        nbtService.setItemString(item, "ignis:chunk", chunkX + "," + chunkZ);
        player.sendActionBar("<gray>Chunk " + chunkX + ", " + chunkZ + "</gray>");
        double size = 8.0;
        IgnisLocation corner = new IgnisLocation(loc.worldId(), loc.worldName(), chunkX * 16.0, loc.y(), chunkZ * 16.0, 0f, 0f);
        TheatricsSupport.pulseRing(world, corner.add(size, 0, size), size, "FLAME");
        world.playSound(loc, "BLOCK_BEACON_AMBIENT", 0.5f, 1.8f);""")

item("atlas_imprinter",
     """int maps = nbtService.getItemInt(item, "ignis:atlas_maps", 0) + 1;
        nbtService.setItemInt(item, "ignis:atlas_maps", maps);
        String stamp = (int) loc.x() + ":" + (int) loc.z();
        nbtService.setItemString(item, "ignis:atlas_" + maps, stamp);
        player.sendMessage("<gold>Atlas imprint <white>#" + maps + "</white> at " + stamp + "</gold>");
        TheatricsSupport.sparkle(world, loc, "COMPOSTER", 8);
        world.playSound(loc, "BLOCK_CARTOGRAPHY_TABLE_USE", 0.8f, 1.1f);""")

print(f"Wrote {len(list(OUT.glob('*.java.template')))} templates to {OUT}")
