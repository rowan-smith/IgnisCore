#!/usr/bin/env python3
"""Scaffold utility extension Java modules for IgnisCore.

Generates pom.xml, manifest YAML, Strategy.java, Behavior.java, and tests.
config.yml is maintained per module — see tools/write-extension-configs.py.
"""

from __future__ import annotations

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BEHAVIORS_DIR = Path(__file__).resolve().parent / "utility-behaviors"

BLOCKS = [
    {"id": "splitter-charge", "name": "Splitter Charge", "kind": "splitter", "type": "fuse", "fuse": 60},
    {"id": "ricochet-tnt", "name": "Ricochet TNT", "kind": "ricochet", "type": "fuse", "fuse": 70},
    {"id": "cascade-mine", "name": "Cascade Mine", "kind": "cascade", "type": "fuse", "fuse": 50},
    {"id": "powder-trail", "name": "Powder Trail", "kind": "powder_trail", "type": "fuse", "fuse": 0, "combustible": True},
    {"id": "ore-sniffer", "name": "Ore Sniffer", "kind": "ore_scan", "type": "placed"},
    {"id": "xp-vacuum", "name": "XP Vacuum", "kind": "xp_vacuum", "type": "placed"},
    {"id": "light-beacon", "name": "Light Beacon", "kind": "light_beacon", "type": "placed"},
    {"id": "crop-accelerator", "name": "Crop Accelerator", "kind": "crop_accel", "type": "placed"},
    {"id": "mob-grinder-hub", "name": "Mob Grinder Hub", "kind": "mob_grinder", "type": "placed"},
    {"id": "bridge-builder", "name": "Bridge Builder", "kind": "bridge_builder", "type": "fuse", "fuse": 40},
    {"id": "scaffold-charge", "name": "Scaffold Charge", "kind": "scaffold", "type": "fuse", "fuse": 50},
    {"id": "repair-station-block", "name": "Repair Station", "kind": "repair_station", "type": "interact"},
    {"id": "waypoint-marker", "name": "Waypoint Marker", "kind": "waypoint", "type": "interact"},
    {"id": "pause-tnt", "name": "Pause TNT", "kind": "pause_fuse", "type": "fuse", "fuse": 100},
    {"id": "accelerating-fuse-tnt", "name": "Accelerating Fuse TNT", "kind": "accelerating_fuse", "type": "fuse", "fuse": 80},
    {"id": "stasis-field", "name": "Stasis Field", "kind": "stasis_field", "type": "placed"},
    {"id": "echo-fuse-tnt", "name": "Echo Fuse TNT", "kind": "echo_fuse", "type": "fuse", "fuse": 80},
    {"id": "last-stand-charge", "name": "Last Stand Charge", "kind": "last_stand", "type": "fuse", "fuse": 0, "combustible": False},
    {"id": "blink-tnt", "name": "Blink TNT", "kind": "blink", "type": "fuse", "fuse": 60},
    {"id": "swap-charge", "name": "Swap Charge", "kind": "swap", "type": "fuse", "fuse": 50},
    {"id": "phase-tnt", "name": "Phase TNT", "kind": "phase", "type": "fuse", "fuse": 60},
    {"id": "rift-generator", "name": "Rift Generator", "kind": "rift", "type": "fuse", "fuse": 80},
    {"id": "pocket-dimension-cache", "name": "Pocket Dimension Cache", "kind": "pocket_cache", "type": "interact", "placed_hooks": True},
    {"id": "mirror-world-tnt", "name": "Mirror World TNT", "kind": "mirror", "type": "fuse", "fuse": 70},
    {"id": "pollinator-block", "name": "Pollinator Block", "kind": "pollinator", "type": "placed"},
    {"id": "scarecrow-anchor", "name": "Scarecrow Anchor", "kind": "scarecrow", "type": "placed"},
    {"id": "compost-heap", "name": "Compost Heap", "kind": "compost", "type": "placed"},
    {"id": "hydroponic-tray", "name": "Hydroponic Tray", "kind": "hydroponic", "type": "placed"},
    {"id": "shepherd-bell", "name": "Shepherd Bell", "kind": "shepherd", "type": "placed"},
    {"id": "milking-station", "name": "Milking Station", "kind": "milking", "type": "placed"},
    {"id": "crop-mri", "name": "Crop MRI", "kind": "crop_mri", "type": "placed"},
    {"id": "chicken-coop-cache", "name": "Chicken Coop Cache", "kind": "chicken_coop", "type": "interact", "placed_hooks": True},
    {"id": "blueprint-projector", "name": "Blueprint Projector", "kind": "blueprint", "type": "placed", "integrations": ["protocol"]},
    {"id": "glow-ink-lantern", "name": "Glow Ink Lantern", "kind": "glow_lantern", "type": "placed"},
    {"id": "auto-sieve", "name": "Auto Sieve", "kind": "auto_sieve", "type": "placed"},
    {"id": "drying-rack", "name": "Drying Rack", "kind": "drying_rack", "type": "placed"},
    {"id": "infuser", "name": "Infuser", "kind": "infuser", "type": "placed"},
    {"id": "recycler", "name": "Recycler", "kind": "recycler", "type": "placed"},
    {"id": "paint-mixer", "name": "Paint Mixer", "kind": "paint_mixer", "type": "placed"},
    {"id": "brewing-accelerator", "name": "Brewing Accelerator", "kind": "brewing_accel", "type": "placed"},
    {"id": "smoker-stack", "name": "Smoker Stack", "kind": "smoker_stack", "type": "placed"},
    {"id": "ore-echo", "name": "Ore Echo", "kind": "ore_scan", "type": "placed"},
    {"id": "mob-radar", "name": "Mob Radar", "kind": "mob_radar", "type": "placed"},
    {"id": "chunk-loader-lite", "name": "Chunk Loader Lite", "kind": "chunk_loader", "type": "interact", "placed_hooks": True},
    {"id": "entity-camera", "name": "Entity Camera", "kind": "entity_camera", "type": "interact", "integrations": ["protocol"]},
    {"id": "player-proximity-alarm", "name": "Player Proximity Alarm", "kind": "proximity_alarm", "type": "placed"},
    {"id": "motion-floodlight", "name": "Motion Floodlight", "kind": "motion_floodlight", "type": "placed"},
    {"id": "moss-creeper", "name": "Moss Creeper", "kind": "moss_creeper", "type": "placed"},
    {"id": "deoxidizer", "name": "Deoxidizer", "kind": "deoxidizer", "type": "placed"},
    {"id": "honey-centrifuge", "name": "Honey Centrifuge", "kind": "honey_centrifuge", "type": "placed"},
    {"id": "kelp-compressor", "name": "Kelp Compressor", "kind": "kelp_compressor", "type": "placed"},
    {"id": "fish-smoker-rack", "name": "Fish Smoker Rack", "kind": "fish_smoker", "type": "placed"},
    {"id": "per-player-weather-dome", "name": "Per-Player Weather Dome", "kind": "weather_dome", "type": "placed", "integrations": ["protocol"]},
    {"id": "item-pedestal-hologram", "name": "Item Pedestal Hologram", "kind": "item_pedestal", "type": "placed", "integrations": ["protocol"]},
    {"id": "secure-trade-table", "name": "Secure Trade Table", "kind": "secure_trade", "type": "interact", "placed_hooks": True},
    {"id": "checkpoint-obelisk", "name": "Checkpoint Obelisk", "kind": "checkpoint", "type": "interact"},
    {"id": "piglin-barter-post", "name": "Piglin Barter Post", "kind": "piglin_barter", "type": "interact", "placed_hooks": True},
    {"id": "prep-counter", "name": "Prep Counter", "kind": "prep_counter", "type": "interact", "placed_hooks": True},
    {"id": "spice-rack", "name": "Spice Rack", "kind": "spice_rack", "type": "interact", "placed_hooks": True},
    {"id": "pizza-oven", "name": "Pizza Oven", "kind": "pizza_oven", "type": "interact", "placed_hooks": True},
    {"id": "ice-cream-freezer", "name": "Ice Cream Freezer", "kind": "ice_cream_freezer", "type": "interact", "placed_hooks": True},
    {"id": "coffee-brewer", "name": "Coffee Brewer", "kind": "coffee_brewer", "type": "interact", "placed_hooks": True},
    {"id": "compost-tea-brewer", "name": "Compost Tea Brewer", "kind": "compost_tea", "type": "interact", "placed_hooks": True},
    {"id": "greenhouse-glass", "name": "Greenhouse Glass", "kind": "greenhouse_glass", "type": "placed"},
    {"id": "irrigation-sprinkler", "name": "Irrigation Sprinkler", "kind": "irrigation_sprinkler", "type": "interact", "placed_hooks": True},
    {"id": "sapling-nursery", "name": "Sapling Nursery", "kind": "sapling_nursery", "type": "interact", "placed_hooks": True},
    {"id": "display-case", "name": "Display Case", "kind": "display_case", "type": "interact", "placed_hooks": True},
    {"id": "ouija-slab", "name": "Ouija Slab", "kind": "ouija_slab", "type": "placed"},
    {"id": "fortune-cookie-maker", "name": "Fortune Cookie Maker", "kind": "fortune_cookie", "type": "interact", "placed_hooks": True},
    {"id": "mood-lantern", "name": "Mood Lantern", "kind": "mood_lantern", "type": "placed"},
    {"id": "lost-and-found-bin", "name": "Lost & Found Bin", "kind": "lost_and_found", "type": "interact", "placed_hooks": True},
    {"id": "socket-lamp", "name": "Socket Lamp", "kind": "socket_lamp", "type": "placed", "profiles": ["placed"]},
    {"id": "keyed-hatch", "name": "Keyed Hatch", "kind": "keyed_hatch", "type": "placed", "profiles": ["placed"]},
    {"id": "sprinkler-head", "name": "Sprinkler Head", "kind": "sprinkler_head", "type": "placed", "profiles": ["placed"]},
    {"id": "barn-bell", "name": "Barn Bell", "kind": "barn_bell", "type": "placed", "profiles": ["placed"]},
    {"id": "pipe-valve", "name": "Pipe Valve", "kind": "pipe_valve", "type": "placed", "profiles": ["placed"]},
    {"id": "picnic-basket", "name": "Picnic Basket", "kind": "picnic_basket", "type": "interact", "placed_hooks": True, "profiles": ["interact", "processing-gui"]},
    {"id": "keg-tap", "name": "Keg Tap", "kind": "keg_tap", "type": "interact", "placed_hooks": True, "profiles": ["interact", "processing-gui"]},
]

ITEMS = [
    {"id": "block-stethoscope", "name": "Block Stethoscope", "kind": "block_stethoscope"},
    {"id": "lock", "name": "Lock", "kind": "lock_item"},
    {"id": "keyring-beacon", "name": "Keyring Beacon", "kind": "keyring_beacon", "integrations": ["nbt-entity"]},
    {"id": "trait-badge", "name": "Trait Badge", "kind": "trait_badge", "integrations": ["nbt-entity"]},
    {"id": "player-chronicle-book", "name": "Player Chronicle Book", "kind": "player_chronicle", "integrations": ["nbt-entity"]},
    {"id": "ore-xray-goggles", "name": "Ore X-Ray Goggles", "kind": "ore_xray_goggles", "integrations": ["protocol"]},
    {"id": "structure-compass", "name": "Structure Compass", "kind": "structure_compass", "integrations": ["nbt-entity"]},
    {"id": "chunk-grid-overlay", "name": "Chunk Grid Overlay", "kind": "chunk_grid_overlay", "integrations": ["protocol"]},
    {"id": "atlas-imprinter", "name": "Atlas Imprinter", "kind": "atlas_imprinter", "integrations": ["nbt-entity"]},
    {"id": "cookie-cutter-stamp", "name": "Cookie Cutter Stamp", "kind": "cookie_cutter", "integrations": ["nbt-entity"]},
    {"id": "rivet-gun", "name": "Rivet Gun", "kind": "rivet_gun"},
    {"id": "paint-stripper", "name": "Paint Stripper", "kind": "paint_stripper"},
    {"id": "stencil-plate", "name": "Stencil Plate", "kind": "stencil_plate"},
    {"id": "sandblaster", "name": "Sandblaster", "kind": "sandblaster"},
    {"id": "mulch-spreader", "name": "Mulch Spreader", "kind": "mulch_spreader"},
    {"id": "gravity-marble", "name": "Gravity Marble", "kind": "gravity_marble"},
    {"id": "quantum-coin", "name": "Quantum Coin", "kind": "quantum_coin", "integrations": ["nbt-entity"]},
    {"id": "lamp-dimmer", "name": "Lamp Dimmer", "kind": "remote_link_item", "integrations": ["nbt-entity"],
     "custom_data": {"linkBlockType": "socket-lamp", "remoteAction": "cycle", "linkRange": 64}},
    {"id": "gate-clicker", "name": "Gate Clicker", "kind": "remote_link_item", "integrations": ["nbt-entity"],
     "custom_data": {"linkBlockType": "keyed-hatch", "remoteAction": "toggle", "linkRange": 64}},
    {"id": "sprinkler-timer", "name": "Sprinkler Timer", "kind": "remote_link_item", "integrations": ["nbt-entity"],
     "custom_data": {"linkBlockType": "sprinkler-head", "remoteAction": "arm", "linkRange": 48}},
    {"id": "farm-call", "name": "Farm Call", "kind": "remote_link_item", "integrations": ["nbt-entity"],
     "custom_data": {"linkBlockType": "barn-bell", "remoteAction": "call", "linkRange": 64}},
    {"id": "valve-wrench", "name": "Valve Wrench", "kind": "remote_link_item", "integrations": ["nbt-entity"],
     "custom_data": {"linkBlockType": "pipe-valve", "remoteAction": "toggle", "linkRange": 48}},
    {"id": "glow-orb", "name": "Glow Orb", "kind": "glow_orb"},
    {"id": "seed-bomb", "name": "Seed Bomb", "kind": "seed_bomb"},
    {"id": "smoke-can", "name": "Smoke Can", "kind": "smoke_can"},
    {"id": "vine-shears", "name": "Vine Shears", "kind": "vine_shears"},
    {"id": "cable-ties", "name": "Cable Ties", "kind": "cable_ties", "integrations": ["nbt-entity"]},
    {"id": "miners-lunch", "name": "Miner's Lunch", "kind": "consumable_item", "integrations": ["nbt-entity"],
     "custom_data": {"cooldownTicks": 12000}},
    {"id": "farmers-tea", "name": "Farmer's Tea", "kind": "consumable_item"},
    {"id": "divers-salt", "name": "Diver's Salt", "kind": "consumable_item", "integrations": ["nbt-entity"]},
    {"id": "cartographers-espresso", "name": "Cartographer's Espresso", "kind": "consumable_item", "integrations": ["nbt-entity"]},
    {"id": "ghost-peppermint", "name": "Ghost Peppermint", "kind": "consumable_item", "integrations": ["nbt-entity"]},
    {"id": "heavy-coat-tonic", "name": "Heavy Coat Tonic", "kind": "consumable_item"},
    {"id": "honey-throat-coat", "name": "Honey Throat Coat", "kind": "consumable_item", "integrations": ["nbt-entity"]},
    {"id": "chorus-bite", "name": "Chorus Bite", "kind": "consumable_item"},
    {"id": "glow-berry-shot", "name": "Glow Berry Shot", "kind": "consumable_item"},
    {"id": "bricklayers-broth", "name": "Bricklayer's Broth", "kind": "consumable_item", "integrations": ["nbt-entity"]},
    {"id": "luck-dust", "name": "Luck Dust", "kind": "consumable_item", "integrations": ["nbt-entity"]},
    {"id": "antidote-swab", "name": "Antidote Swab", "kind": "consumable_item"},
    {"id": "unlabeled-potion", "name": "Unlabeled Potion", "kind": "consumable_item", "integrations": ["nbt-entity"]},
]


def pkg(id_: str) -> str:
    return re.sub(r"[^a-z0-9]", "", id_.lower())


def cls(id_: str) -> str:
    parts = re.split(r"[-_]", id_)
    return "".join(p[:1].upper() + p[1:] for p in parts if p)


def write(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")


def integration_yaml(integrations: list[str] | None) -> str:
    if not integrations:
        return ""
    lines = ["requires-integrations:"]
    for i in integrations:
        lines.append(f"  - {i}")
    return "\n".join(lines) + "\n"


def profiles_yaml(profiles: list[str] | None) -> str:
    if not profiles:
        return ""
    lines = ["profiles:"]
    for profile in profiles:
        lines.append(f"  - {profile}")
    return "\n".join(lines) + "\n"


def block_pom(artifact: str) -> str:
    return f"""<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <parent>
    <groupId>dev.rono.blocks</groupId>
    <artifactId>blocks</artifactId>
    <version>${{revision}}</version>
  </parent>
  <artifactId>{artifact}</artifactId>
  <packaging>jar</packaging>
  <dependencies>
    <dependency>
      <groupId>dev.rono</groupId>
      <artifactId>api</artifactId>
      <scope>provided</scope>
    </dependency>
  </dependencies>
</project>
"""


def item_pom(artifact: str) -> str:
    return block_pom(artifact).replace("dev.rono.blocks", "dev.rono.items", 1).replace("<artifactId>blocks</artifactId>", "<artifactId>items</artifactId>", 1)


def load_behavior(kind: str, class_name: str, package: str, is_item: bool) -> str:
    path = BEHAVIORS_DIR / f"{kind}.java.template"
    if not path.exists():
        raise FileNotFoundError(kind)
    text = path.read_text(encoding="utf-8")
    pkg_prefix = "item" if is_item else "block"
    return text.replace("{{PACKAGE}}", package).replace("{{CLASS}}", class_name).replace(
        "dev.rono.igniscore.block.{{PACKAGE}}", f"dev.rono.igniscore.{pkg_prefix}.{package}")


def block_strategy(package: str, class_name: str, ext: dict) -> str:
    ext_type = ext["type"]
    fuse = ext.get("fuse", 80)
    combustible = ext.get("combustible", ext_type == "fuse")
    register_lines = []

    if ext_type == "fuse":
        register_lines.append("        onBlockTick(event -> behavior.onTick(event.instance()));")
        register_lines.append("        onBlockTrigger(event -> behavior.onTrigger(event.instance(), event.triggerContext()));")
    if ext_type == "placed":
        register_lines.append("        onBlockPlace(event -> behavior.onPlaced(event.definition(), event.location()));")
        register_lines.append("        onBlockBreak(event -> behavior.onPlacedBreak(event.definition(), event.location()));")
    if ext_type == "interact":
        register_lines.append("        onBlockInteract(event -> behavior.onPlacedInteract(event.definition(), event.location(), event.player(), event.interaction(), event.heldItem(), event.action()));")
        if ext.get("placed_hooks"):
            register_lines.append("        onBlockPlace(event -> behavior.onPlaced(event.definition(), event.location()));")
            register_lines.append("        onBlockBreak(event -> behavior.onPlacedBreak(event.definition(), event.location()));")

    if ext_type == "fuse":
        if combustible:
            profile_method = f"""    @Override
    public StrategyProfile profile(BlockDefinition definition) {{
        return StrategyProfile.fuse({fuse});
    }}"""
        else:
            profile_method = f"""    @Override
    public StrategyProfile profile(BlockDefinition definition) {{
        return StrategyProfile.builder()
                .defaultFuse({fuse})
                .combustible(false)
                .build();
    }}"""
    else:
        profile_method = """    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.placed();
    }"""

    register_block = "\n".join(register_lines)

    return f"""package dev.rono.igniscore.block.{package};

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;

public class Strategy extends AbstractIgnisBlockStrategy {{
    private final {class_name}Behavior behavior;

    public Strategy(IgnisStrategyContext context) {{
        super(context);
        this.behavior = new {class_name}Behavior(context);
    }}

{profile_method}

    @Override
    public void registerEvents() {{
{register_block}
    }}
}}
"""


def item_strategy(package: str, class_name: str) -> str:
    return f"""package dev.rono.igniscore.item.{package};

import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {{
    private final {class_name}Behavior behavior;

    public Strategy(IgnisStrategyContext context) {{
        super(context);
        this.behavior = new {class_name}Behavior(context);
    }}

    @Override
    public void registerEvents() {{
        onItemClick(event -> {{
            if ("use".equals(event.actionToken())) {{
                behavior.onItemUse(event.player(), event.definition(), event.item(), event.clickedBlock());
            }}
        }});
    }}
}}
"""


def strategy_test_block(package: str, ext_id: str) -> str:
    return f"""package dev.rono.igniscore.block.{package};

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.strategy.StrategyProfile;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StrategyTest {{
    @Test
    void manifestMatchesExtensionId() {{
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "block-extension.yml");
        assertEquals("{ext_id}", manifest.getId());
    }}

    @Test
    void strategyExposesProfile() {{
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(StrategyTest.class, "{ext_id}", 10001);
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        assertNotNull(strategy.profile(definition));
    }}
}}
"""


def strategy_test_item(package: str, ext_id: str) -> str:
    return f"""package dev.rono.igniscore.item.{package};

import dev.rono.igniscore.api.extension.ExtensionManifest;
import dev.rono.igniscore.testsupport.ExtensionTestSupport;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StrategyTest {{
    @Test
    void manifestMatchesExtensionId() {{
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "item-extension.yml");
        assertEquals("{ext_id}", manifest.getId());
    }}
}}
"""


def generate_block(ext: dict) -> None:
    package = pkg(ext["id"])
    class_name = cls(ext["id"])
    base = ROOT / "extensions" / "blocks" / ext["id"]
    write(base / "pom.xml", block_pom(ext["id"]))
    write(base / "src/main/resources/block-extension.yml", f"""id: {ext['id']}
name: {ext['name']}
version: @project.version@
api-version: @ignis.api.version@
author: IgnisCore
strategy: dev.rono.igniscore.block.{package}.Strategy
{integration_yaml(ext.get('integrations'))}{profiles_yaml(ext.get('profiles'))}""")
    write(base / f"src/main/java/dev/rono/igniscore/block/{package}/Strategy.java", block_strategy(package, class_name, ext))
    write(base / f"src/main/java/dev/rono/igniscore/block/{package}/{class_name}Behavior.java", load_behavior(ext["kind"], class_name, package, False))
    write(base / f"src/test/java/dev/rono/igniscore/block/{package}/StrategyTest.java", strategy_test_block(package, ext["id"]))


def generate_item(ext: dict) -> None:
    package = pkg(ext["id"])
    class_name = cls(ext["id"])
    base = ROOT / "extensions" / "items" / ext["id"]
    write(base / "pom.xml", item_pom(ext["id"]))
    write(base / "src/main/resources/item-extension.yml", f"""id: {ext['id']}
name: {ext['name']}
version: @project.version@
api-version: @ignis.api.version@
author: IgnisCore
strategy: dev.rono.igniscore.item.{package}.Strategy
{integration_yaml(ext.get('integrations'))}{profiles_yaml(ext.get('profiles'))}""")
    write(base / f"src/main/java/dev/rono/igniscore/item/{package}/Strategy.java", item_strategy(package, class_name))
    write(base / f"src/main/java/dev/rono/igniscore/item/{package}/{class_name}Behavior.java", load_behavior(ext["kind"], class_name, package, True))
    write(base / f"src/test/java/dev/rono/igniscore/item/{package}/StrategyTest.java", strategy_test_item(package, ext["id"]))


def main() -> None:
    for ext in BLOCKS:
        print("block:", ext["id"])
        generate_block(ext)
    for ext in ITEMS:
        print("item:", ext["id"])
        generate_item(ext)
    print("Done.")


if __name__ == "__main__":
    main()
