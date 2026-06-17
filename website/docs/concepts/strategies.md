---
title: Strategies
description: Strategy classes, event subscriptions, and the IgnisStrategyRegistry.
slug: /concepts/strategies
---

Every extension JAR provides a **strategy** class listed in its manifest. Strategies implement block or item behavior and receive a shared `IgnisStrategyContext`.

## Class contract

Blocks extend `AbstractIgnisBlockStrategy`:

```java
public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
        context.eventBus().subscribe(new MyOnBlockPlaceListener(context));
        context.eventBus().subscribe(new MyOnBlockTriggerListener(context));
    }
}
```

Items extend `AbstractIgnisItemStrategy` and subscribe to `OnItemClickListener` in the constructor.

Subscribe to lifecycle events in the strategy constructor via `context.eventBus().subscribe(...)`.

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

## Behavior config and clicks

YAML `behavior` sections declare combustibility, ignition materials, and placement/ignite sounds. Click routing is handled by per-module `OnBlockClickListener` classes subscribed in the strategy constructor.

Fuse timing and explosion radius live in `custom_data` and are read at runtime by the core and your listeners.

## Registry

Manifest `id` values register in `IgnisStrategyRegistry`. Config `id` values map to in-game types via `BlockManager` and `ItemManager`.

## Related

- [Block lifecycle](/concepts/blocks) — placed vs active phases
- [Item lifecycle](/concepts/items) — behavior tokens and `ItemBehaviorConfig`
- [API Reference](/developers/api/core-api) — event bus and strategy interfaces
- [Extension Cookbook](/developers/cookbook) — copy-paste recipes
- [Javadoc](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/package-summary.html)
