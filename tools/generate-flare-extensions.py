#!/usr/bin/env python3
"""Scaffold flare explosive extensions for IgnisCore."""

from __future__ import annotations

import os
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

BLOCKS = [
    {"id": "orbit-tnt", "name": "Orbit TNT", "fuse": 80, "kind": "orbit"},
    {"id": "black-hole-tnt", "name": "Black Hole TNT", "fuse": 100, "kind": "black_hole", "integrations": ["region"]},
    {"id": "trampoline-tnt", "name": "Trampoline TNT", "fuse": 60, "kind": "trampoline"},
    {"id": "magnet-tnt", "name": "Magnet TNT", "fuse": 80, "kind": "magnet"},
    {"id": "anti-gravity-zone", "name": "Anti-Gravity Zone", "fuse": 0, "kind": "antigravity", "combustible": True, "placed": True},
    {"id": "slingshot-tnt", "name": "Slingshot TNT", "fuse": 60, "kind": "slingshot"},
    {"id": "tether-tnt", "name": "Tether TNT", "fuse": 80, "kind": "tether"},
    {"id": "centrifuge-tnt", "name": "Centrifuge TNT", "fuse": 70, "kind": "centrifuge"},
    {"id": "featherfall-charge", "name": "Featherfall Charge", "fuse": 50, "kind": "featherfall"},
    {"id": "inferno-tnt", "name": "Inferno TNT", "fuse": 80, "kind": "inferno"},
    {"id": "frost-tnt", "name": "Frost TNT", "fuse": 60, "kind": "frost"},
    {"id": "lightning-rod-tnt", "name": "Lightning Rod TNT", "fuse": 60, "kind": "lightning"},
    {"id": "acid-tnt", "name": "Acid TNT", "fuse": 60, "kind": "acid"},
    {"id": "mudslide-tnt", "name": "Mudslide TNT", "fuse": 70, "kind": "mudslide"},
    {"id": "wildfire-seed", "name": "Wildfire Seed", "fuse": 40, "kind": "wildfire"},
    {"id": "ember-mine", "name": "Ember Mine", "fuse": 0, "kind": "ember_mine", "combustible": False, "placed": True},
    {"id": "tsunami-charge", "name": "Tsunami Charge", "fuse": 60, "kind": "tsunami"},
    {"id": "poison-cloud-tnt", "name": "Poison Cloud TNT", "fuse": 60, "kind": "poison"},
    {"id": "solar-flare-tnt", "name": "Solar Flare TNT", "fuse": 70, "kind": "solar"},
    {"id": "bouncing-betty", "name": "Bouncing Betty", "fuse": 0, "kind": "bouncing_betty", "combustible": False, "placed": True},
    {"id": "claymore-mine", "name": "Claymore Mine", "fuse": 0, "kind": "claymore", "combustible": False, "placed": True},
    {"id": "remote-c4", "name": "Remote C4", "fuse": 0, "kind": "remote_c4", "combustible": False},
    {"id": "timed-charge", "name": "Timed Charge", "fuse": 100, "kind": "timed", "combustible": False, "integrations": ["hologram"]},
    {"id": "tripwire-charge", "name": "Tripwire Charge", "fuse": 0, "kind": "tripwire", "combustible": False, "placed": True},
    {"id": "breaching-charge", "name": "Breaching Charge", "fuse": 50, "kind": "breaching"},
    {"id": "mirage-tnt", "name": "Mirage TNT", "fuse": 80, "kind": "mirage", "integrations": ["protocol"]},
    {"id": "hologram-tnt", "name": "Hologram TNT", "fuse": 80, "kind": "hologram", "integrations": ["protocol"]},
    {"id": "silent-tnt", "name": "Silent TNT", "fuse": 60, "kind": "silent", "integrations": ["protocol"]},
    {"id": "doppelganger-block", "name": "Doppelgänger Block", "fuse": 0, "kind": "doppelganger", "combustible": True, "placed": True, "integrations": ["protocol"]},
    {"id": "fake-bedrock", "name": "Fake Bedrock", "fuse": 0, "kind": "fake_bedrock", "combustible": True, "placed": True, "integrations": ["protocol"]},
    {"id": "scare-charge", "name": "Scare Charge", "fuse": 0, "kind": "scare", "combustible": False, "integrations": ["protocol"]},
    {"id": "echo-blast", "name": "Echo Blast", "fuse": 60, "kind": "echo", "integrations": ["protocol"]},
    {"id": "glitch-tnt", "name": "Glitch TNT", "fuse": 80, "kind": "glitch", "integrations": ["protocol"]},
    {"id": "invisiwall", "name": "Invisiwall", "fuse": 0, "kind": "invisiwall", "combustible": True, "placed": True, "integrations": ["protocol"]},
    {"id": "screen-shake-charge", "name": "Screen Shake Charge", "fuse": 40, "kind": "screen_shake", "integrations": ["protocol"]},
]

ITEMS = [
    {"id": "cryo-grenade", "name": "Cryo Grenade", "kind": "cryo"},
    {"id": "smoke-grenade", "name": "Smoke Grenade", "kind": "smoke"},
    {"id": "sticky-bomb", "name": "Sticky Bomb", "kind": "sticky"},
    {"id": "decoy-flare", "name": "Decoy Flare", "kind": "decoy"},
    {"id": "phantom-grenade", "name": "Phantom Grenade", "kind": "phantom_grenade", "integrations": ["protocol"]},
]


def pkg(id_: str) -> str:
    return re.sub(r"[^a-z0-9]", "", id_.lower())


def cls(id_: str) -> str:
    parts = re.split(r"[-_]", id_)
    return "".join(p[:1].upper() + p[1:] for p in parts if p)


def write(path: Path, content: str) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding="utf-8")



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


def integration_yaml(integrations: list[str] | None) -> str:
    if not integrations:
        return ""
    mapping = {"protocol": "protocol", "hologram": "hologram", "region": "region"}
    lines = ["requires-integrations:"]
    for i in integrations:
        lines.append(f"  - {mapping.get(i, i)}")
    return "\n".join(lines) + "\n"


def block_manifest(ext: dict, package: str) -> str:
    return f"""id: {ext['id']}
name: {ext['name']}
version: @project.version@
api-version: @ignis.api.version@
author: IgnisCore
strategy: dev.rono.igniscore.block.{package}.Strategy
{integration_yaml(ext.get('integrations'))}"""


def item_manifest(ext: dict, package: str) -> str:
    return f"""id: {ext['id']}
name: {ext['name']}
version: @project.version@
api-version: @ignis.api.version@
author: IgnisCore
strategy: dev.rono.igniscore.item.{package}.Strategy
{integration_yaml(ext.get('integrations'))}"""


def block_config(ext: dict) -> str:
    combustible = ext.get("combustible", True)
    fuse = ext.get("fuse", 80)
    placed = ext.get("placed", False)
    kind = ext["kind"]
    remote = kind in ("remote_c4", "scare", "tripwire")
    if kind == "timed":
        behavior = """behavior:
  combustible: false
  left_click_block: none
  right_click_block: none
  sounds:
    place: BLOCK_TNT_PLACE"""
        fuse = ext.get("fuse", 100)
    elif remote:
        behavior = """behavior:
  combustible: false
  left_click_block: none
  right_click_block: none
  sounds:
    place: BLOCK_TNT_PLACE"""
    elif placed and not combustible:
        behavior = """behavior:
  combustible: false
  left_click_block: break
  right_click_block: none
  sounds:
    place: BLOCK_METAL_PLACE"""
    elif placed:
        behavior = """behavior:
  combustible: true
  left_click_block: break
  right_click_block: ignite
  ignition_materials:
    - FLINT_AND_STEEL
    - FIRE_CHARGE
  sounds:
    place: BLOCK_STONE_PLACE
    ignite: ITEM_FLINTANDSTEEL_USE"""
    else:
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
    return f"""id: {ext['id']}

display:
  title: "&6{ext['name']}"
  description:
    - "&7Flare explosive"

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
  power: 4.0
  fire: false
  blockDamage: true
"""


def item_config(ext: dict) -> str:
    return f"""id: {ext['id']}

display:
  title: "&6{ext['name']}"
  description:
    - "&7Throwable flare explosive"

item:
  base_material: snowball

textures:
  icon: icon.png

behavior:
  right_click_air: throw
  right_click_block: throw

custom_data:
  power: 3.0
  fuse_ticks: 40
  throw_velocity: 1.2
  fire: false
"""


def block_strategy(package: str, class_name: str, ext: dict) -> str:
    kind = ext["kind"]
    has_tick = kind not in (
        "remote_c4", "scare", "ember_mine", "claymore", "bouncing_betty", "tripwire",
        "antigravity", "doppelganger", "fake_bedrock", "invisiwall",
        "featherfall", "wildfire", "breaching", "echo", "screen_shake",
    )
    has_placed = ext.get("placed", False)
    fuse = ext.get("fuse", 80)
    combustible = ext.get("combustible", True)

    imports = []
    methods = []

    if has_tick:
        methods.append("    @Override\n    public void onTick(RuntimeBlockInstance instance) {\n        behavior.onTick(instance);\n    }")

    if has_placed:
        methods.append("    @Override\n    public void onPlaced(BlockDefinition definition, IgnisLocation location) {\n        behavior.onPlaced(definition, location);\n    }")
        if kind in ("ember_mine", "bouncing_betty", "claymore", "tripwire"):
            methods.append("    @Override\n    public void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {\n        behavior.onPlacedBreak(location);\n    }")

    profile_parts = [f".defaultFuse({fuse})"]
    if not combustible:
        profile_parts.append(".combustible(false)")

    profile = f"""    @Override
    public StrategyProfile profile(BlockDefinition definition) {{
        return StrategyProfile.builder()
                {''.join(profile_parts)}
                .build();
    }}
"""

    placed_import = "\nimport dev.rono.igniscore.api.port.IgnisLocation;" if has_placed else ""
    return f"""package dev.rono.igniscore.block.{package};

import dev.rono.igniscore.api.model.BlockDefinition;
import dev.rono.igniscore.api.model.RuntimeBlockInstance;
import dev.rono.igniscore.api.strategy.AbstractIgnisBlockStrategy;
import dev.rono.igniscore.api.strategy.IgnisStrategyContext;
import dev.rono.igniscore.api.strategy.StrategyProfile;{placed_import}

public class Strategy extends AbstractIgnisBlockStrategy {{
    private final {class_name}Behavior behavior;

    public Strategy(IgnisStrategyContext context) {{
        super(context);
        this.behavior = new {class_name}Behavior(context);
    }}

{profile}
{chr(10).join(methods)}

    @Override
    public void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {{
        behavior.onTrigger(instance, triggerContext);
    }}
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
            if ("throw".equals(token)) {{
                behavior.onItemUse(player, definition, item);
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
    private static final String EXTENSION_ID = "{ext_id}";

    @Test
    void manifestMatchesExtensionId() {{
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "block-extension.yml");
        assertEquals(EXTENSION_ID, manifest.getId());
        assertEquals("dev.rono.igniscore.block.{package}.Strategy", manifest.getStrategyClass());
    }}

    @Test
    void strategyExposesProfileForConfig() {{
        BlockDefinition definition = ExtensionTestSupport.loadBlockDefinition(StrategyTest.class, EXTENSION_ID, 10001);
        Strategy strategy = new Strategy(ExtensionTestSupport.noopContext());
        StrategyProfile profile = strategy.profile(definition);
        assertNotNull(profile);
        assertEquals(EXTENSION_ID, definition.getId());
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
    private static final String EXTENSION_ID = "{ext_id}";

    @Test
    void manifestMatchesExtensionId() {{
        ExtensionManifest manifest = ExtensionTestSupport.loadManifest(StrategyTest.class, "item-extension.yml");
        assertEquals(EXTENSION_ID, manifest.getId());
        assertEquals("dev.rono.igniscore.item.{package}.Strategy", manifest.getStrategyClass());
    }}
}}
"""


# Behavior sources loaded from companion module
BEHAVIORS_DIR = Path(__file__).parent / "flare-behaviors"


def load_behavior(kind: str, class_name: str, package: str, is_item: bool) -> str:
    suffix = "item" if is_item else "block"
    path = BEHAVIORS_DIR / f"{kind}.java.template"
    if not path.exists():
        raise FileNotFoundError(f"Missing behavior template: {path}")
    text = path.read_text(encoding="utf-8")
    return (
        text.replace("{{PACKAGE}}", package)
        .replace("{{CLASS}}", class_name)
        .replace("{{KIND}}", kind)
    )


def generate_block(ext: dict) -> None:
    package = pkg(ext["id"])
    class_name = cls(ext["id"])
    base = ROOT / "extensions" / "blocks" / ext["id"]
    write(base / "pom.xml", block_pom(ext["id"]))
    write(base / "src/main/resources/block-extension.yml", block_manifest(ext, package))
    write(base / "src/main/resources/config.yml", block_config(ext))
    write(base / f"src/main/java/dev/rono/igniscore/block/{package}/Strategy.java", block_strategy(package, class_name, ext))
    write(base / f"src/main/java/dev/rono/igniscore/block/{package}/{class_name}Behavior.java", load_behavior(ext["kind"], class_name, package, False))
    write(base / f"src/test/java/dev/rono/igniscore/block/{package}/StrategyTest.java", strategy_test_block(package, ext["id"]))


def generate_item(ext: dict) -> None:
    package = pkg(ext["id"])
    class_name = cls(ext["id"])
    base = ROOT / "extensions" / "items" / ext["id"]
    write(base / "pom.xml", item_pom(ext["id"]))
    write(base / "src/main/resources/item-extension.yml", item_manifest(ext, package))
    write(base / "src/main/resources/config.yml", item_config(ext))
    write(base / f"src/main/java/dev/rono/igniscore/item/{package}/Strategy.java", item_strategy(package, class_name))
    write(base / f"src/main/java/dev/rono/igniscore/item/{package}/{class_name}Behavior.java", load_behavior(ext["kind"], class_name, package, True))
    write(base / f"src/test/java/dev/rono/igniscore/item/{package}/StrategyTest.java", strategy_test_item(package, ext["id"]))


def main() -> None:
    for ext in BLOCKS:
        print(f"block: {ext['id']}")
        generate_block(ext)
    for ext in ITEMS:
        print(f"item: {ext['id']}")
        generate_item(ext)
    print("Done.")


if __name__ == "__main__":
    main()
