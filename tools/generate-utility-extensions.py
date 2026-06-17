#!/usr/bin/env python3
"""Scaffold utility extension modules for IgnisCore."""

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


def block_config(ext: dict) -> str:
    ext_type = ext["type"]
    combustible = ext.get("combustible", ext_type == "fuse")
    if ext_type == "interact":
        behavior = """behavior:
  combustible: false
  left_click_block: break
  right_click_block: open
  sounds:
    place: BLOCK_METAL_PLACE"""
    elif ext_type == "placed":
        behavior = """behavior:
  combustible: false
  left_click_block: break
  right_click_block: none
  sounds:
    place: BLOCK_AMETHYST_BLOCK_CHIME"""
    elif combustible:
        behavior = """behavior:
  combustible: true
  left_click_block: break
  right_click_block: ignite
  ignition_materials:
    - FLINT_AND_STEEL
    - FIRE_CHARGE
  sounds:
    place: BLOCK_TNT_PLACE
    ignite: ITEM_FLINTANDSTEEL_USE"""
    else:
        behavior = """behavior:
  combustible: false
  left_click_block: break
  right_click_block: none
  sounds:
    place: BLOCK_TNT_PLACE"""
    fuse = ext.get("fuse", 80)
    return f"""id: {ext['id']}

display:
  title: "&b{ext['name']}"
  description:
    - "&7Utility extension"

block:
  placeable: true
  breakable: true
  breaking:
    ticks: 0
    break_sound: BLOCK_GRAVEL_BREAK
    hit_sound: BLOCK_GRAVEL_HIT

textures:
  top: top.png
  side: side.png
  bottom: bottom.png

model:
  type: "block_display"
  mode: "auto"

{behavior}

custom_data:
  fuse: {fuse}
  power: 3.5
  radius: 8.0
"""


def item_config(ext: dict) -> str:
    return f"""id: {ext['id']}

display:
  title: "&b{ext['name']}"
  description:
    - "&7Utility item"

item:
  base_material: paper

textures:
  icon: icon.png

behavior:
  right_click_air: use
  right_click_block: use

custom_data:
  radius: 8.0
"""


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
    methods = []
    imports_extra = ""
    trigger_method = ""

    if ext_type == "fuse":
        methods.append("    @Override\n    public void onTick(RuntimeBlockInstance instance) {\n        behavior.onTick(instance);\n    }")
        trigger_method = """
    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
        behavior.onTrigger(instance, triggerContext);
    }"""
    if ext_type == "placed":
        imports_extra = "\nimport dev.rono.igniscore.api.port.IgnisLocation;"
        methods.append("    @Override\n    public void onPlaced(BlockDefinition definition, IgnisLocation location) {\n        behavior.onPlaced(definition, location);\n    }")
        methods.append("    @Override\n    public void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {\n        behavior.onPlacedBreak(definition, location);\n    }")
    if ext_type == "interact":
        imports_extra = "\nimport dev.rono.igniscore.api.CustomBlockAction;\nimport dev.rono.igniscore.api.port.IgnisItem;\nimport dev.rono.igniscore.api.port.IgnisLocation;\nimport dev.rono.igniscore.api.port.IgnisPlayer;"
        methods.append("""    @Override
    public void onPlacedInteract(BlockDefinition definition,
                                 IgnisLocation location,
                                 IgnisPlayer player,
                                 dev.rono.igniscore.api.port.IgnisInteraction interaction,
                                 IgnisItem heldItem,
                                 CustomBlockAction action) {
        behavior.onPlacedInteract(definition, location, player, interaction, heldItem, action);
    }""")
        if ext.get("placed_hooks"):
            methods.append("    @Override\n    public void onPlaced(BlockDefinition definition, IgnisLocation location) {\n        behavior.onPlaced(definition, location);\n    }")
            methods.append("    @Override\n    public void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {\n        behavior.onPlacedBreak(definition, location);\n    }")

    profile_parts = [f".defaultFuse({fuse})"]
    if not combustible:
        profile_parts.append(".combustible(false)")

    return f"""package dev.rono.igniscore.block.{package};

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;{imports_extra}

public class Strategy extends AbstractIgnisBlockStrategy {{
    private final {class_name}Behavior behavior;

    public Strategy(IgnisStrategyContext context) {{
        super(context);
        this.behavior = new {class_name}Behavior(context);
    }}

    @Override
    public StrategyProfile profile(BlockDefinition definition) {{
        return StrategyProfile.builder()
                {''.join(profile_parts)}
                .build();
    }}

{chr(10).join(methods)}
{trigger_method}
}}
"""


def item_strategy(package: str, class_name: str) -> str:
    return f"""package dev.rono.igniscore.item.{package};

import dev.rono.igniscore.api.config.ItemBehaviorConfig;
import dev.rono.igniscore.api.model.ItemDefinition;
import dev.rono.igniscore.api.port.IgnisBlock;
import dev.rono.igniscore.api.port.IgnisInteraction;
import dev.rono.igniscore.api.port.IgnisItem;
import dev.rono.igniscore.api.port.IgnisPlayer;
import dev.rono.igniscore.api.strategy.AbstractIgnisItemStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;

public class Strategy extends AbstractIgnisItemStrategy {{
    private final {class_name}Behavior behavior;

    public Strategy(IgnisStrategyContext context) {{
        super(context);
        this.behavior = new {class_name}Behavior(context);
    }}

    @Override
    public void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                           IgnisInteraction action, IgnisBlock clickedBlock) {{
        ItemBehaviorConfig config = ItemBehaviorConfig.from(definition.getBehaviorConfig());
        config.actionFor(action).ifPresent(token -> {{
            if ("use".equals(token)) {{
                behavior.onItemUse(player, definition, item, clickedBlock);
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
{integration_yaml(ext.get('integrations'))}""")
    write(base / "src/main/resources/config.yml", block_config(ext))
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
{integration_yaml(ext.get('integrations'))}""")
    write(base / "src/main/resources/config.yml", item_config(ext))
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
