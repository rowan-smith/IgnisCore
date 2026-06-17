#!/usr/bin/env python3
"""Write per-module extension config.yml display/behavior sections from the catalog.

custom_data keys are validated separately — after editing behaviors or configs run:

    python3 tools/audit-extension-configs.py

To change a single extension, prefer editing its config.yml in place.
"""

from __future__ import annotations

from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

COMMON_BLOCK = """block:
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
"""

COMMON_ITEM = """item:
  base_material: paper

textures:
  icon: icon.png

behavior:
  right_click_air: use
  right_click_block: use
"""

BEHAVIOR = {
    "placed": """behavior:
  left_click_block: break
  right_click_block: none
  sounds:
    place: BLOCK_AMETHYST_BLOCK_CHIME""",
    "interact": """behavior:
  left_click_block: break
  right_click_block: open
  sounds:
    place: BLOCK_METAL_PLACE""",
    "fuse": """behavior:
  combustible: true
  left_click_block: break
  right_click_block: ignite
  ignition_materials:
    - FLINT_AND_STEEL
    - FIRE_CHARGE
  sounds:
    place: BLOCK_TNT_PLACE
    ignite: ITEM_FLINTANDSTEEL_USE""",
    "fuse_manual": """behavior:
  combustible: false
  left_click_block: break
  right_click_block: none
  sounds:
    place: BLOCK_TNT_PLACE""",
}


def yaml_custom(data: dict) -> str:
    if not data:
        return ""
    lines = ["", "custom_data:"]
    for key, value in data.items():
        if isinstance(value, str):
            lines.append(f'  {key}: "{value}"')
        elif isinstance(value, bool):
            lines.append(f"  {key}: {'true' if value else 'false'}")
        else:
            lines.append(f"  {key}: {value}")
    return "\n".join(lines)


def render_block(ext_id: str, spec: dict) -> str:
    desc = spec.get("desc", ["&7Utility block."])
    body = f"""id: {ext_id}

display:
  title: "{spec['title']}"
  description:
"""
    for line in desc:
        body += f'    - "{line}"\n'
    body += f"\n{COMMON_BLOCK}\n\n{BEHAVIOR[spec['behavior']]}\n"
    body += yaml_custom(spec.get("custom_data", {})).lstrip("\n")
    return body.rstrip() + "\n"


def render_item(ext_id: str, spec: dict) -> str:
    desc = spec.get("desc", ["&7Utility item."])
    body = f"""id: {ext_id}

display:
  title: "{spec['title']}"
  description:
"""
    for line in desc:
        body += f'    - "{line}"\n'
    body += f"\n{COMMON_ITEM}\n"
    body += yaml_custom(spec.get("custom_data", {})).lstrip("\n")
    return body.rstrip() + "\n"


# ---------------------------------------------------------------------------
# Per-module catalog — edit entries individually.
# ---------------------------------------------------------------------------

BLOCKS: dict[str, dict] = {
    "splitter-charge": {
        "title": "&bSplitter Charge",
        "desc": ["&7Four cardinal offset blasts on detonation."],
        "behavior": "fuse",
        "custom_data": {"fuse": 60, "splitOffset": 2.5, "power": 4.0},
    },
    "ricochet-tnt": {
        "title": "&bRicochet TNT",
        "desc": ["&7Bouncing ray of explosions along facing."],
        "behavior": "fuse",
        "custom_data": {"fuse": 70, "bounces": 4, "step": 2.5, "power": 3.0},
    },
    "cascade-mine": {
        "title": "&bCascade Mine",
        "desc": ["&7Main blast plus delayed ring waves."],
        "behavior": "fuse",
        "custom_data": {"fuse": 50, "cascadeWaves": 4, "cascadeDelay": 6, "power": 3.5},
    },
    "powder-trail": {
        "title": "&bPowder Trail",
        "desc": ["&7Smoke trail while fusing, then detonates."],
        "behavior": "fuse",
        "custom_data": {"fuse": 0, "trailStep": 0.6, "power": 4.0, "fire": False},
    },
    "ore-sniffer": {
        "title": "&bOre Sniffer",
        "desc": ["&7Beams toward the nearest ore in range."],
        "behavior": "placed",
        "custom_data": {"scanRadius": 12},
    },
    "xp-vacuum": {
        "title": "&bXP Vacuum",
        "desc": ["&7Pulls experience orbs toward the block."],
        "behavior": "placed",
        "custom_data": {"vacuumRadius": 6.0, "vacuumStrength": 0.35},
    },
    "light-beacon": {
        "title": "&bLight Beacon",
        "desc": ["&7Pulsing glow ring while placed."],
        "behavior": "placed",
        "custom_data": {"beaconRadius": 3.0},
    },
    "crop-accelerator": {
        "title": "&bCrop Accelerator",
        "desc": ["&7Bonemeals crops in a radius."],
        "behavior": "placed",
        "custom_data": {"cropRadius": 4},
    },
    "mob-grinder-hub": {
        "title": "&bMob Grinder Hub",
        "desc": ["&7Pulls and damages hostiles in range."],
        "behavior": "placed",
        "custom_data": {"grindRadius": 5.0},
    },
    "bridge-builder": {
        "title": "&bBridge Builder",
        "desc": ["&7Lays a bridge while fusing, then explodes."],
        "behavior": "fuse",
        "custom_data": {"fuse": 40, "bridgeLength": 6, "oakBridge": True, "power": 2.5},
    },
    "scaffold-charge": {
        "title": "&bScaffold Charge",
        "desc": ["&7Builds upward scaffolding, then detonates."],
        "behavior": "fuse",
        "custom_data": {"fuse": 50, "scaffoldHeight": 4, "power": 3.0},
    },
    "repair-station-block": {
        "title": "&bRepair Station",
        "desc": ["&7Right-click with a held item to repair durability."],
        "behavior": "interact",
        "custom_data": {"repairAmount": 25},
    },
    "waypoint-marker": {
        "title": "&bWaypoint Marker",
        "desc": ["&7Saves a named waypoint at this location."],
        "behavior": "interact",
    },
    "pause-tnt": {
        "title": "&bPause TNT",
        "desc": ["&7Fuse pauses mid-countdown before detonating."],
        "behavior": "fuse",
        "custom_data": {"fuse": 100, "pauseDuration": 20, "power": 4.5},
    },
    "accelerating-fuse-tnt": {
        "title": "&bAccelerating Fuse TNT",
        "desc": ["&7Fuse ticks get faster and louder over time."],
        "behavior": "fuse",
        "custom_data": {"fuse": 80, "power": 5.0},
    },
    "stasis-field": {
        "title": "&bStasis Field",
        "desc": ["&7Freezes entities within radius while placed."],
        "behavior": "placed",
        "custom_data": {"stasisRadius": 4.5},
    },
    "echo-fuse-tnt": {
        "title": "&bEcho Fuse TNT",
        "desc": ["&7Main blast followed by weaker echo bursts."],
        "behavior": "fuse",
        "custom_data": {"fuse": 80, "echoBursts": 3, "echoDelay": 10, "power": 4.0},
    },
    "last-stand-charge": {
        "title": "&bLast Stand Charge",
        "desc": ["&7Freezes nearby entities, then erupts. Ignite manually."],
        "behavior": "fuse_manual",
        "custom_data": {"fuse": 0, "stasisRadius": 5.0, "lastStandTicks": 30, "lastStandPower": 6.0},
    },
    "blink-tnt": {
        "title": "&bBlink TNT",
        "desc": ["&7Random teleports during fuse, then explodes."],
        "behavior": "fuse",
        "custom_data": {"fuse": 60, "blinkInterval": 14, "blinkRadius": 5.0, "power": 3.5},
    },
    "swap-charge": {
        "title": "&bSwap Charge",
        "desc": ["&7Swaps nearest players on detonation."],
        "behavior": "fuse",
        "custom_data": {"fuse": 50, "swapRadius": 8.0, "power": 2.5},
    },
    "phase-tnt": {
        "title": "&bPhase TNT",
        "desc": ["&7Phase burst passes through blocks in radius."],
        "behavior": "fuse",
        "custom_data": {"fuse": 60, "phaseRadius": 6.0, "power": 4.0},
    },
    "rift-generator": {
        "title": "&bRift Generator",
        "desc": ["&7Pulls entities inward while fusing."],
        "behavior": "fuse",
        "custom_data": {"fuse": 80, "riftPull": 0.12, "riftRadius": 7.0, "power": 4.5},
    },
    "pocket-dimension-cache": {
        "title": "&bPocket Dimension Cache",
        "desc": ["&7Per-player storage chest."],
        "behavior": "interact",
        "custom_data": {"storageRows": 3},
    },
    "mirror-world-tnt": {
        "title": "&bMirror World TNT",
        "desc": ["&7Mirrors explosion across a horizontal plane."],
        "behavior": "fuse",
        "custom_data": {"fuse": 70, "power": 4.0},
    },
    "pollinator-block": {
        "title": "&bPollinator Block",
        "desc": ["&7Bonemeals crops in a small radius."],
        "behavior": "placed",
        "custom_data": {"pollinateRadius": 3},
    },
    "scarecrow-anchor": {
        "title": "&bScarecrow Anchor",
        "desc": ["&7Pushes hostile mobs away from the anchor."],
        "behavior": "placed",
        "custom_data": {"scareRadius": 8.0},
    },
    "compost-heap": {
        "title": "&bCompost Heap",
        "desc": ["&7Ambient compost particles while placed."],
        "behavior": "placed",
    },
    "hydroponic-tray": {
        "title": "&bHydroponic Tray",
        "desc": ["&7Waters and bonemeals nearby crops."],
        "behavior": "placed",
        "custom_data": {"hydroRadius": 2},
    },
    "shepherd-bell": {
        "title": "&bShepherd Bell",
        "desc": ["&7Herd passive animals toward the block."],
        "behavior": "placed",
        "custom_data": {"herdRadius": 10.0},
    },
    "milking-station": {
        "title": "&bMilking Station",
        "desc": ["&7Milking effects when livestock are nearby."],
        "behavior": "placed",
    },
    "crop-mri": {
        "title": "&bCrop MRI",
        "desc": ["&7Scans crop density and chimes results."],
        "behavior": "placed",
        "custom_data": {"mriRadius": 6},
    },
    "chicken-coop-cache": {
        "title": "&bChicken Coop Cache",
        "desc": ["&7Collects eggs from nearby chickens into storage."],
        "behavior": "interact",
        "custom_data": {"coopRadius": 6.0},
    },
    "blueprint-projector": {
        "title": "&bBlueprint Projector",
        "desc": ["&7Scanning beams as a build guide."],
        "behavior": "placed",
    },
    "glow-ink-lantern": {
        "title": "&bGlow Ink Lantern",
        "desc": ["&7Emits glow particles while placed."],
        "behavior": "placed",
    },
    "auto-sieve": {
        "title": "&bAuto Sieve",
        "desc": ["&7Sifting particles and sounds while placed."],
        "behavior": "placed",
    },
    "drying-rack": {
        "title": "&bDrying Rack",
        "desc": ["&7Drying smoke while placed."],
        "behavior": "placed",
    },
    "infuser": {
        "title": "&bInfuser",
        "desc": ["&7Enchanting particle theatrics while placed."],
        "behavior": "placed",
    },
    "recycler": {
        "title": "&bRecycler",
        "desc": ["&7Scrap grinding particles while placed."],
        "behavior": "placed",
    },
    "paint-mixer": {
        "title": "&bPaint Mixer",
        "desc": ["&7Colorful mixing particles while placed."],
        "behavior": "placed",
    },
    "brewing-accelerator": {
        "title": "&bBrewing Accelerator",
        "desc": ["&7Brewing stand theatrics while placed."],
        "behavior": "placed",
    },
    "smoker-stack": {
        "title": "&bSmoker Stack",
        "desc": ["&7Rising smoke stack while placed."],
        "behavior": "placed",
    },
    "ore-echo": {
        "title": "&bOre Echo",
        "desc": ["&7Beams toward the nearest ore in range."],
        "behavior": "placed",
        "custom_data": {"scanRadius": 12},
    },
    "mob-radar": {
        "title": "&bMob Radar",
        "desc": ["&7Alerts when hostiles enter radar range."],
        "behavior": "placed",
        "custom_data": {"radarRadius": 16.0},
    },
    "chunk-loader-lite": {
        "title": "&bChunk Loader Lite",
        "desc": ["&7Force-loads chunk while fueled."],
        "behavior": "interact",
        "custom_data": {"tickPeriod": 40},
    },
    "entity-camera": {
        "title": "&bEntity Camera",
        "desc": ["&7Spectate nearest passive mob on interact."],
        "behavior": "interact",
        "custom_data": {"cameraRadius": 12.0, "cameraDurationTicks": 100},
    },
    "player-proximity-alarm": {
        "title": "&bPlayer Proximity Alarm",
        "desc": ["&7Rings when players enter alarm range."],
        "behavior": "placed",
        "custom_data": {"alarmRadius": 12.0},
    },
    "motion-floodlight": {
        "title": "&bMotion Floodlight",
        "desc": ["&7Beams light toward nearby players."],
        "behavior": "placed",
        "custom_data": {"motionRadius": 8.0},
    },
    "moss-creeper": {
        "title": "&bMoss Creeper",
        "desc": ["&7Spreads moss on blocks near water."],
        "behavior": "placed",
        "custom_data": {"mossRadius": 3},
    },
    "deoxidizer": {
        "title": "&bDeoxidizer",
        "desc": ["&7Cleans oxidized copper in radius."],
        "behavior": "placed",
        "custom_data": {"deoxidizeRadius": 4},
    },
    "honey-centrifuge": {
        "title": "&bHoney Centrifuge",
        "desc": ["&7Honey drip theatrics while placed."],
        "behavior": "placed",
    },
    "kelp-compressor": {
        "title": "&bKelp Compressor",
        "desc": ["&7Bubble effects while placed."],
        "behavior": "placed",
    },
    "fish-smoker-rack": {
        "title": "&bFish Smoker Rack",
        "desc": ["&7Smoking particles while placed."],
        "behavior": "placed",
    },
    "per-player-weather-dome": {
        "title": "&bPer-Player Weather Dome",
        "desc": ["&7Local cloud and rain dome."],
        "behavior": "placed",
        "custom_data": {"domeRadius": 5.0},
    },
    "item-pedestal-hologram": {
        "title": "&bItem Pedestal Hologram",
        "desc": ["&7Enchant sparkle hologram on pedestal."],
        "behavior": "placed",
    },
    "secure-trade-table": {
        "title": "&bSecure Trade Table",
        "desc": ["&7Two-player trade window; confirm with lime dye."],
        "behavior": "interact",
    },
    "checkpoint-obelisk": {
        "title": "&bCheckpoint Obelisk",
        "desc": ["&7Records player checkpoint on interact."],
        "behavior": "interact",
    },
    "piglin-barter-post": {
        "title": "&bPiglin Barter Post",
        "desc": ["&7Trades gold ingots for nether loot."],
        "behavior": "interact",
        "custom_data": {"tickPeriod": 30},
    },
    "prep-counter": {
        "title": "&bPrep Counter",
        "desc": ["&7Combines three foods into a golden carrot."],
        "behavior": "interact",
        "custom_data": {"tickPeriod": 40},
    },
    "spice-rack": {
        "title": "&bSpice Rack",
        "desc": ["&7Spice + food grants haste on open."],
        "behavior": "interact",
    },
    "pizza-oven": {
        "title": "&bPizza Oven",
        "desc": ["&7Bakes bread, beetroot, and milk into cake."],
        "behavior": "interact",
        "custom_data": {"tickPeriod": 50},
    },
    "ice-cream-freezer": {
        "title": "&bIce Cream Freezer",
        "desc": ["&7Crafts ice cream; eating grants fire resistance."],
        "behavior": "interact",
        "custom_data": {"tickPeriod": 60},
    },
    "coffee-brewer": {
        "title": "&bCoffee Brewer",
        "desc": ["&7Brews cocoa into speed potions."],
        "behavior": "interact",
        "custom_data": {"tickPeriod": 35},
    },
    "compost-tea-brewer": {
        "title": "&bCompost Tea Brewer",
        "desc": ["&7Splash compost tea bonemeals nearby crops."],
        "behavior": "interact",
        "custom_data": {"tickPeriod": 45, "cropRadius": 4},
    },
    "greenhouse-glass": {
        "title": "&bGreenhouse Glass",
        "desc": ["&7Bonemeals crops under glass roof."],
        "behavior": "placed",
        "custom_data": {"greenhouseRadius": 2},
    },
    "irrigation-sprinkler": {
        "title": "&bIrrigation Sprinkler",
        "desc": ["&7Consumes water to irrigate farmland."],
        "behavior": "interact",
        "custom_data": {"tickPeriod": 40, "waterRadius": 4},
    },
    "sapling-nursery": {
        "title": "&bSapling Nursery",
        "desc": ["&7Auto-plants saplings from storage."],
        "behavior": "interact",
        "custom_data": {"tickPeriod": 80},
    },
    "display-case": {
        "title": "&bDisplay Case",
        "desc": ["&7Museum display; announces item on open."],
        "behavior": "interact",
    },
    "ouija-slab": {
        "title": "&bOuija Slab",
        "desc": ["&7Spells letters when players stand on corners."],
        "behavior": "placed",
        "custom_data": {"minPlayers": 2},
    },
    "fortune-cookie-maker": {
        "title": "&bFortune Cookie Maker",
        "desc": ["&7Crafts fortune cookies from wheat and paper."],
        "behavior": "interact",
        "custom_data": {"tickPeriod": 40},
    },
    "mood-lantern": {
        "title": "&bMood Lantern",
        "desc": ["&7Particle mood shifts with nearby mobs."],
        "behavior": "placed",
        "custom_data": {"moodRadius": 10.0},
    },
    "lost-and-found-bin": {
        "title": "&bLost & Found Bin",
        "desc": ["&7Sweeps nearby item drops into storage."],
        "behavior": "interact",
        "custom_data": {"collectRadius": 8.0},
    },
    "socket-lamp": {
        "title": "&bSocket Lamp",
        "desc": ["&7Remote-cyclable lamp; light level 0–15."],
        "behavior": "placed",
    },
    "keyed-hatch": {
        "title": "&bKeyed Hatch",
        "desc": ["&7Remote-toggle iron hatch or bars."],
        "behavior": "placed",
    },
    "sprinkler-head": {
        "title": "&bSprinkler Head",
        "desc": ["&7Armed sprinkler irrigates farmland."],
        "behavior": "placed",
        "custom_data": {"waterRadius": 4},
    },
    "barn-bell": {
        "title": "&bBarn Bell",
        "desc": ["&7Remote-call herds passive livestock."],
        "behavior": "placed",
        "custom_data": {"callCooldownTicks": 200, "herdRadius": 24.0},
    },
    "pipe-valve": {
        "title": "&bPipe Valve",
        "desc": ["&7Remote-toggle cosmetic water flow."],
        "behavior": "placed",
    },
    "picnic-basket": {
        "title": "&bPicnic Basket",
        "desc": ["&7Shared consumable storage; duo open grants saturation."],
        "behavior": "interact",
    },
    "keg-tap": {
        "title": "&bKeg Tap",
        "desc": ["&7Refills drink items from bucket input."],
        "behavior": "interact",
        "custom_data": {"tickPeriod": 40},
    },
}

ITEMS: dict[str, dict] = {
    "block-stethoscope": {
        "title": "&bBlock Stethoscope",
        "desc": ["&7Reveals targeted block material."],
    },
    "lock": {
        "title": "&bLock",
        "desc": ["&7Toggles engaged/disengaged lock state."],
    },
    "keyring-beacon": {
        "title": "&bKeyring Beacon",
        "desc": ["&7Cycles beacon slots saving coordinates."],
        "custom_data": {"beaconSlots": 3},
    },
    "trait-badge": {
        "title": "&bTrait Badge",
        "desc": ["&7Rolls and stores a random trait."],
    },
    "player-chronicle-book": {
        "title": "&bPlayer Chronicle Book",
        "desc": ["&7Writes location chronicle pages."],
    },
    "ore-xray-goggles": {
        "title": "&bOre X-Ray Goggles",
        "desc": ["&7Pings nearest ore in range."],
        "custom_data": {"xrayRadius": 14},
    },
    "structure-compass": {
        "title": "&bStructure Compass",
        "desc": ["&7Shows stored compass bearing."],
    },
    "chunk-grid-overlay": {
        "title": "&bChunk Grid Overlay",
        "desc": ["&7Highlights current chunk boundaries."],
    },
    "atlas-imprinter": {
        "title": "&bAtlas Imprinter",
        "desc": ["&7Stamps coordinates into atlas imprints."],
    },
    "cookie-cutter-stamp": {
        "title": "&bCookie Cutter Stamp",
        "desc": ["&7Cycles cookie stamp shapes."],
    },
    "rivet-gun": {
        "title": "&bRivet Gun",
        "desc": ["&7Reinforces clicked block to obsidian."],
        "custom_data": {"reinforcedMaterial": "obsidian"},
    },
    "paint-stripper": {
        "title": "&bPaint Stripper",
        "desc": ["&7Strips banners or dye from blocks."],
    },
    "stencil-plate": {
        "title": "&bStencil Plate",
        "desc": ["&7Applies glow stencil to concrete powder."],
    },
    "sandblaster": {
        "title": "&bSandblaster",
        "desc": ["&7Etches smooth stone into chiseled bricks."],
        "custom_data": {"etchRadius": 1},
    },
    "mulch-spreader": {
        "title": "&bMulch Spreader",
        "desc": ["&7Mossifies paths or spreads moss near water."],
    },
    "gravity-marble": {
        "title": "&bGravity Marble",
        "desc": ["&7Fast throwable marble with sparkle trail."],
        "custom_data": {"marbleSpeed": 1.2},
    },
    "quantum-coin": {
        "title": "&bQuantum Coin",
        "desc": ["&7Flips heads or tails."],
    },
    "lamp-dimmer": {
        "title": "&bLamp Dimmer",
        "desc": ["&7Link to socket lamp; cycle brightness remotely."],
        "custom_data": {"linkBlockType": "socket-lamp", "remoteAction": "cycle", "linkRange": 64},
    },
    "gate-clicker": {
        "title": "&bGate Clicker",
        "desc": ["&7Link to keyed hatch; toggle from 64 blocks."],
        "custom_data": {"linkBlockType": "keyed-hatch", "remoteAction": "toggle", "linkRange": 64},
    },
    "sprinkler-timer": {
        "title": "&bSprinkler Timer",
        "desc": ["&7Link to sprinkler head; arm/disarm remotely."],
        "custom_data": {"linkBlockType": "sprinkler-head", "remoteAction": "arm", "linkRange": 48},
    },
    "farm-call": {
        "title": "&bFarm Call",
        "desc": ["&7Link to barn bell; call livestock remotely."],
        "custom_data": {"linkBlockType": "barn-bell", "remoteAction": "call", "linkRange": 64},
    },
    "valve-wrench": {
        "title": "&bValve Wrench",
        "desc": ["&7Link to pipe valve; toggle flow remotely."],
        "custom_data": {"linkBlockType": "pipe-valve", "remoteAction": "toggle", "linkRange": 48},
    },
    "glow-orb": {
        "title": "&bGlow Orb",
        "desc": ["&7Throwable; emits light particles ~60s."],
        "custom_data": {"throwSpeed": 1.0, "glowDurationTicks": 1200},
    },
    "seed-bomb": {
        "title": "&bSeed Bomb",
        "desc": ["&7Throwable; scatters grass and flowers."],
        "custom_data": {"throwSpeed": 1.1, "scatterRadius": 3},
    },
    "smoke-can": {
        "title": "&bSmoke Can",
        "desc": ["&7Throwable; thick smoke wall, zero damage."],
        "custom_data": {"throwSpeed": 0.9, "smokeDurationTicks": 400},
    },
    "vine-shears": {
        "title": "&bVine Shears",
        "desc": ["&7Cuts connected vine clusters from one click."],
        "custom_data": {"maxVines": 32},
    },
    "cable-ties": {
        "title": "&bCable Ties",
        "desc": ["&7Decorative particle cable between fence posts."],
    },
    "miners-lunch": {
        "title": "&bMiner's Lunch",
        "desc": ["&7Haste + saturation; 10-minute cooldown."],
        "custom_data": {"cooldownTicks": 12000},
    },
    "farmers-tea": {
        "title": "&bFarmer's Tea",
        "desc": ["&7Instant bonemeal on nearby crops."],
        "custom_data": {"cropRadius": 5},
    },
    "divers-salt": {
        "title": "&bDiver's Salt",
        "desc": ["&7Water breathing for 90 seconds."],
    },
    "cartographers-espresso": {
        "title": "&bCartographer's Espresso",
        "desc": ["&7Speed + compass heading toward ruins."],
    },
    "ghost-peppermint": {
        "title": "&bGhost Peppermint",
        "desc": ["&7Phantoms ignore you until next sleep."],
    },
    "heavy-coat-tonic": {
        "title": "&bHeavy Coat Tonic",
        "desc": ["&7Slowness I + Resistance II for 45s."],
    },
    "honey-throat-coat": {
        "title": "&bHoney Throat Coat",
        "desc": ["&7Clears poison; sweet scent mark on mobs."],
    },
    "chorus-bite": {
        "title": "&bChorus Bite",
        "desc": ["&7Random 8-block sideways warp."],
    },
    "glow-berry-shot": {
        "title": "&bGlow Berry Shot",
        "desc": ["&7Night vision + player glow 120s."],
    },
    "bricklayers-broth": {
        "title": "&bBricklayer's Broth",
        "desc": ["&7Haste II; faster block placement 90s."],
    },
    "luck-dust": {
        "title": "&bLuck Dust",
        "desc": ["&7Sprinkle near chest for bonus loot roll."],
    },
    "antidote-swab": {
        "title": "&bAntidote Swab",
        "desc": ["&7Clears one negative effect."],
    },
    "unlabeled-potion": {
        "title": "&bUnlabeled Potion",
        "desc": ["&7Random harmless effect; identifies after first drink."],
    },
}


def main() -> None:
    for ext_id, spec in BLOCKS.items():
        path = ROOT / "extensions" / "blocks" / ext_id / "src/main/resources/config.yml"
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(render_block(ext_id, spec), encoding="utf-8")
        print("block:", ext_id)
    for ext_id, spec in ITEMS.items():
        path = ROOT / "extensions" / "items" / ext_id / "src/main/resources/config.yml"
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(render_item(ext_id, spec), encoding="utf-8")
        print("item:", ext_id)
    print("Done.")


if __name__ == "__main__":
    main()
