---
title: Strategies
description: Strategy classes, profiles, and the IgnisStrategyRegistry.
slug: /concepts/strategies
---

Every extension JAR provides a **strategy** class listed in its manifest. Strategies implement block or item behavior and receive a shared `IgnisStrategyContext`.

## Class contract

Blocks extend `AbstractIgnisBlockStrategy`:

```java
public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return IgnisStrategies.blocks().placed();
    }

    @Override
    public void registerEvents() {
        onBlockPlace(event -> { /* placed hook */ });
        onBlockTrigger(event -> { /* detonation */ });
    }
}
```

Items extend `AbstractIgnisItemStrategy` and subscribe with `onItemClick` instead.

Legacy strategy override methods (`onPlaced`, `onTrigger`, `onItemUse`, `onPlacedClick`, …) were removed in 1.0.0. Implement **`registerEvents()`** and subscribe via the scoped helpers on `AbstractIgnisStrategy`.

## Event bus

| Helper | Event | Typical use |
|--------|-------|-------------|
| `onBlockPlace` | `BlockPlaceEvent` | Spawn visuals, start scheduler |
| `onBlockClick` | `BlockClickEvent` | Custom click handling |
| `onBlockInteract` | `BlockInteractEvent` | GUI open follow-up |
| `onBlockBreak` | `BlockBreakEvent` | Cleanup on break |
| `onBlockActivate` | `BlockActivateEvent` | Active fuse instance created |
| `onBlockTick` | `BlockTickEvent` | Fuse countdown |
| `onBlockTrigger` | `BlockTriggerEvent` | Detonation / trigger |
| `onItemClick` | `ItemClickEvent` | Throw, assign, detonate, use |

Integrators observe the same bus globally via `IgnisCoreAPI.eventBus()` (or `IgnisCoreFacade.eventBus()` on a bound facade). See [Core API — Event bus](/developers/api/core-api#event-bus).

## IgnisStrategyContext

Prefer the short accessors on `IgnisStrategyContext`:

| Accessor | Service | Purpose |
|----------|---------|---------|
| `scheduler()` | `IgnisScheduler` | Async and sync tasks |
| `nbt()` | `IgnisNbtService` | Read/write NBT on items and entities |
| `protocol()` | `IgnisProtocolService` | Packet-level hooks when available |
| `effects()` | `IgnisEffectService` | Particles, sounds, fake explosions |
| `extensions()` | `ExtensionSupport` | Inventories, drop collectors, world bridge |
| `eventBus()` | `IgnisEventBus` | Scoped subscriptions (prefer helpers above) |

## Strategy profiles

YAML `behavior` sections merge into the strategy **profile** at runtime. The core routes standard click actions before firing **`onBlockClick`** / **`onItemClick`**.

Use `IgnisStrategies.blocks().placed()` (or `StrategyProfile.placed()`) for utility and interact blocks. Use `.fuse(ticks)` or `.combustible(fuse, radius)` when the block participates in the fuse lifecycle. Fuse and explosion radius are opt-in on `StrategyProfile` — omit them for placed-only blocks.

Subscribe with `onBlockClick` or `onItemClick` when you need logic beyond declared behavior tokens.

## Registry

Manifest `id` values register in `IgnisStrategyRegistry`. Config `id` values map to in-game types via `BlockManager` and `ItemManager`. The loader calls `registerEvents()` after binding the manifest descriptor.

## Related

- [Block lifecycle](/concepts/blocks) — placed vs active phases
- [Item lifecycle](/concepts/items) — behavior tokens and `ItemBehaviorConfig`
- [API Reference](/developers/api/core-api) — event bus and strategy interfaces
- [Extension Cookbook](/developers/cookbook) — copy-paste recipes
- [Javadoc](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/package-summary.html)
