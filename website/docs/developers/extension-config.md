---
title: Extension config.yml
description: What belongs in block and item extension config.yml — and what does not.
slug: /developers/extension-config
---

Each extension JAR ships a `config.yml` beside its manifest. Only include keys your strategy actually reads.

## Block sections

| Section | Required | Purpose |
|---------|----------|---------|
| `id` | Yes | In-game type id (`/ignis give`, NBT type key) |
| `display` | Yes | Title and lore |
| `block` | Yes | Placeable/breakable flags, mining sounds |
| `textures` | Yes | Face asset paths |
| `model` | Optional | Authoring metadata for pack tooling |
| `behavior` | Yes | Surface click routing |
| `custom_data` | Optional | Extension-specific tuning only |

## Item sections

| Section | Required | Purpose |
|---------|----------|---------|
| `id` | Yes | In-game type id |
| `display` | Yes | Title and lore |
| `item` | Yes | Base vanilla material |
| `textures` | Yes | Icon path |
| `behavior` | Yes | Click action tokens |
| `custom_data` | Optional | Extension-specific tuning only |

## `behavior` — blocks

`combustible` defaults to **false**. Only set it when the block should be ignitable.

```yaml
# Placed utility block (auto-sieve, socket-lamp, crop accelerator, …)
behavior:
  left_click_block: break
  right_click_block: none
  sounds:
    place: BLOCK_AMETHYST_BLOCK_CHIME
```

```yaml
# Interact / GUI block (picnic-basket, keg-tap, secure-trade-table, …)
behavior:
  left_click_block: break
  right_click_block: open
  sounds:
    place: BLOCK_METAL_PLACE
```

```yaml
# Explosive fuse block — combustible + ignite required
behavior:
  combustible: true
  left_click_block: break
  right_click_block: ignite
  ignition_materials:
    - FLINT_AND_STEEL
    - FIRE_CHARGE
  sounds:
    place: BLOCK_TNT_PLACE
    ignite: ITEM_FLINTANDSTEEL_USE
```

| Action token | Effect |
|--------------|--------|
| `none` | No action |
| `break` | Break the placed block |
| `ignite` | Start fuse / active phase |
| `open` | Open extension GUI |
| `handled` | Strategy handles the click |

## `custom_data` — only what you use

`custom_data` is a free-form map merged into `BlockDefinition.getCustomData()` / `ItemDefinition.getCustomData()`. Read values in strategy code with `StrategySupport`:

```java
int tickPeriod = StrategySupport.customInt(definition.getCustomData(), "tickPeriod", 20);
double range = StrategySupport.customDouble(definition.getCustomData(), "linkRange", 64.0);
```

**Do not copy unrelated keys.** A placed crop block does not need `fuse`, `power`, or `radius`. An explosive block does not need `linkBlockType`.

### Examples by extension kind

| Kind | Typical `custom_data` keys |
|------|--------------------------|
| Fuse / explosive | `fuse`, `power` or `radius`, `fire`, `blockDamage` |
| Placed tick block | `tickPeriod`, `scanRadius`, `herdRadius`, … |
| Link item | `linkBlockType`, `remoteAction`, `linkRange` |
| Throwable item | `throwSpeed`, `fuse_ticks`, `scatterRadius`, … |
| Consumable | `cooldownTicks`, `cropRadius` |
| Non-tuned item | *(omit section entirely)* |

Bundled reference configs with inline comments:

- [nuke](https://github.com/%%site.repo%%/blob/main/extensions/blocks/nuke/src/main/resources/config.yml) — full explosive tuning
- [grenade](https://github.com/%%site.repo%%/blob/main/extensions/items/grenade/src/main/resources/config.yml) — throwable tuning
- [auto-sieve](https://github.com/%%site.repo%%/blob/main/extensions/blocks/auto-sieve/src/main/resources/config.yml) — no `custom_data`
- [lamp-dimmer](https://github.com/%%site.repo%%/blob/main/extensions/items/lamp-dimmer/src/main/resources/config.yml) — link item keys only

## Bundled extension catalog

The utility suite under `extensions/blocks/` and `extensions/items/` includes:

| Category | Examples |
|----------|----------|
| Explosives / fuse | `splitter-charge`, `blink-tnt`, `nuke` |
| Placed utilities | `auto-sieve`, `crop-accelerator`, `socket-lamp` |
| Interact / GUI | `picnic-basket`, `keg-tap`, `chunk-loader-lite` |
| Link pairs | `lamp-dimmer` + `socket-lamp`, `gate-clicker` + `keyed-hatch` |
| Throwables | `glow-orb`, `seed-bomb`, `smoke-can` |
| Consumables | `miners-lunch`, `unlabeled-potion`, `farmers-tea` |

See the [Extension Cookbook](/developers/cookbook) for raw Java patterns that read these keys.

## Maintaining utility extension configs

Bundled utility extensions keep `config.yml` **in each module**. `generate-utility-extensions.py` does not write configs.

| Tool | Purpose |
|------|---------|
| `tools/audit-extension-configs.py` | **Verify every key** each Behavior template reads is present in `custom_data` |
| `tools/audit-extension-configs.py --check` | CI check — fails if any key is missing |
| `tools/write-extension-configs.py` | Edit display/behavior sections in the catalog, then sync |
| `extensions/*/src/main/resources/config.yml` | Preferred place to edit a single extension |

After changing a behavior template, run the audit script to catch missing keys (e.g. `sieveParticles` on auto-sieve):

```bash
python3 tools/audit-extension-configs.py
```

Hand-authored reference extensions (`nuke`, `grenade`, `quarry-cache`, tactical TNT) are edited in place only.

## Related

- [Block lifecycle](/concepts/blocks) — placed vs active phases
- [Item lifecycle](/concepts/items) — click routing
- [Extension Cookbook](/developers/cookbook) — strategy recipes
- [Core API](/developers/api/core-api) — `StrategySupport`, definitions
