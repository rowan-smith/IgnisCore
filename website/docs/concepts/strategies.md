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
}
```

Items extend `AbstractIgnisItemStrategy` instead.

## IgnisStrategyContext

Prefer the short accessors on `IgnisStrategyContext`:

| Accessor | Service | Purpose |
|----------|---------|---------|
| `scheduler()` | `IgnisScheduler` | Async and sync tasks |
| `nbt()` | `IgnisNbtService` | Read/write NBT on items and entities |
| `protocol()` | `IgnisProtocolService` | Packet-level hooks when available |
| `effects()` | `IgnisEffectService` | Particles, sounds, fake explosions |
| `extensions()` | `ExtensionSupport` | Inventories, drop collectors, world bridge |

## Strategy profiles

YAML `behavior` sections merge into the strategy **profile** at runtime. The core routes standard click actions before calling strategy overrides.

Override `onPlacedClick` (blocks) or `onItemUse` (items) only when you need logic beyond declared behavior tokens.

## Registry

Manifest `id` values register in `IgnisStrategyRegistry`. Config `id` values map to in-game types via `BlockManager` and `ItemManager`.

## Related

- [Block lifecycle](/concepts/blocks) — placed vs active callbacks
- [Item lifecycle](/concepts/items) — behavior tokens and `ItemBehaviorConfig`
- [API Reference](/developers/api/core-api) — strategy interfaces
- [Javadoc](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/package-summary.html)
