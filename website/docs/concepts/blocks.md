---
title: Block Lifecycle
description: Placed and active phases for custom IgnisCore blocks — fuse explosives, utility blocks, and interact blocks.
slug: /concepts/blocks
---

Custom blocks exist as barrier blocks in the world with two distinct phases. Bundled extensions range from fuse explosives and tactical TNT to placed utility blocks (auto-sieve, crop accelerators) and interact blocks with GUIs.

## Phases

| Phase | When | Event bus hooks |
|-------|------|-----------------|
| **Placed** | Barrier block exists in world | `onBlockPlace`, `onBlockClick`, `onBlockInteract`, `onBlockBreak` |
| **Active** | After ignition / fuse | `onBlockActivate`, `onBlockTick`, `onBlockTrigger` |

Subscribe in `registerEvents()` using helpers on `AbstractIgnisBlockStrategy` (`onBlockPlace`, `onBlockClick`, …). See [Core API — Event bus](/developers/api/core-api#event-bus).

Surface click routing is declared in YAML `behavior`. The core resolves tokens with `BlockBehaviorConfig.from(definition.getBehaviorConfig())` and fires **`onBlockClick`** on the event bus.

Use `onBlockClick` when you need custom logic beyond standard actions (for example returning `CustomBlockAction.OPEN` or handling `handled` tokens).

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

## Behavior and clicks

Declare combustibility, ignition materials, and sounds in `behavior`. Wire click routing with `OnBlockClickListener` subscriptions (for example `PlacedClickListener.combustible()` or `PlacedClickListener.fixed(...)`). Fuse and radius belong in `custom_data`.

## Persistence

Placed blocks are indexed in `placed-blocks.json` — see [Storage](/storage).

## Related

- [Extensions](/concepts/extensions) — manifest and deploy
- [Strategies](/concepts/strategies) — `AbstractIgnisBlockStrategy` and event bus
- [Extension config](/developers/extension-config) — which keys belong in `config.yml`
- [Extension Cookbook](/developers/cookbook) — strategy recipes
