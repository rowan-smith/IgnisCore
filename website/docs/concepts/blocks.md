---
title: Block Lifecycle
description: Placed and active phases for custom IgnisCore blocks — fuse explosives, utility blocks, and interact blocks.
slug: /concepts/blocks
---

Custom blocks exist as barrier blocks in the world with two distinct phases. Bundled extensions range from fuse explosives and tactical TNT to placed utility blocks (auto-sieve, crop accelerators) and interact blocks with GUIs.

## Phases

| Phase | When | Callbacks |
|-------|------|-----------|
| **Placed** | Barrier block exists in world | `onPlaced`, `onPlacedClick`, `onPlacedInteract`, `onPlacedBreak` |
| **Active** | After ignition / fuse | `onPlace`, `onTick`, `onTrigger` |

Surface click routing is declared in YAML `behavior`. Bundled blocks resolve tokens with `BlockBehaviorConfig.from(definition.getBehaviorConfig())` before calling strategy overrides.

Override `onPlacedClick` only for custom logic beyond standard actions.

## Config sections

| Section | Purpose |
|---------|---------|
| `display` | Title and description |
| `block` | Placeable/breakable flags, base material, breaking tuning |
| `textures` | Asset paths (top, side, bottom) |
| `model` | Optional authoring metadata for pack tooling |
| `interactions` | Optional per-face click overrides |
| `block_display` | Optional display-entity tuning |
| `behavior` | Surface clicks — left/right block and air actions |
| `custom_data` | Optional — only keys your strategy reads (see [Extension config](/developers/extension-config)) |

## Block behavior

`combustible` defaults to **false**. Only include it (set to `true`) for ignitable fuse blocks.

```yaml
behavior:
  combustible: true
  left_click_block: break
  right_click_block: ignite
  left_click_air: none
  right_click_air: none
  ignition_materials:
    - FLINT_AND_STEEL
    - FIRE_CHARGE
  sounds:
    place: BLOCK_BEACON_ACTIVATE
    ignite: ITEM_FLINTANDSTEEL_USE
```

| Action token | Effect |
|--------------|--------|
| `none` | No action |
| `break` | Break the placed block |
| `ignite` | Start fuse / active phase |
| `open` | Open extension GUI |
| `handled` | Strategy handles the click |

## Persistence

Placed blocks are indexed in `placed-blocks.json` — see [Storage](/storage).

## Related

- [Extensions](/concepts/extensions) — manifest and deploy
- [Strategies](/concepts/strategies) — `AbstractIgnisBlockStrategy`
- [Extension config](/developers/extension-config) — which keys belong in `config.yml`
- [Extension Cookbook](/developers/cookbook) — strategy recipes
