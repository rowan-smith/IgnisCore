---
title: API Layers
description: How IgnisCore splits the public API for extension authors and integrators.
slug: /developers/api/layers
---

IgnisCore exposes one Maven artifact (`dev.rono:api`) with four intentional layers. Pick the lowest layer you need — extension code should never reach internal runtime types.

## Layer map

```
┌─────────────────────────────────────────────────────────┐
│  L4  Integrator API          IgnisCoreAPI               │
│      Other plugins on the server                        │
├─────────────────────────────────────────────────────────┤
│  L3  Extension runtime       IgnisStrategyContext       │
│      ExtensionSupport, IgnisScheduler, services          │
├─────────────────────────────────────────────────────────┤
│  L2  Extension contract      IgnisBlockStrategy         │
│                              IgnisItemStrategy          │
│      Models + YAML             BlockDefinition, config  │
├─────────────────────────────────────────────────────────┤
│  L1  Platform ports          IgnisWorld, IgnisPlayer…   │
│      No Bukkit / Sponge imports                         │
├─────────────────────────────────────────────────────────┤
│  L0  Internal (non-API)      PlatformAdapter, loaders  │
└─────────────────────────────────────────────────────────┘
```

## Who uses what

| Audience | Entry point | Packages |
|----------|-------------|----------|
| **Extension author** | `IgnisStrategyContext` | `strategy`, `model`, `config`, `port`, `service`, `inventory`, `collection` |
| **Integrator** | `IgnisCoreAPI` | Same ports/services for handles returned by the facade |
| **Core contributor** | Guice modules, loaders | `common/`, `bukkit/`, `sponge/` — not in the API jar |

## Extension author rules

1. Implement a strategy class with constructor `(IgnisStrategyContext context)`.
2. Use `context.scheduler()`, `context.nbt()`, `context.effects()`, `context.protocol()`, and `context.extensions()` — not `IgnisCoreAPI`.
3. Use port types (`IgnisPlayer`, `IgnisWorld`) in method signatures; never import platform classes.
4. Declare `requires-integrations` and `profiles` in your manifest — see [Extension profiles](/developers/extension-profiles).

## Integrator rules

1. Depend on `api` with `provided` scope.
2. Call `IgnisCoreAPI` after IgnisCore has enabled (same classloader as the plugin).
3. Use `IgnisCoreAPI.createItem`, `triggerBlock`, `getPlacedBlockType`, etc. for cross-plugin automation.

## Optional helpers

`dev.rono.extensions:shared` provides typed config and behavior helpers used by bundled extensions. It is **not** required for third-party JARs — copy patterns from the [cookbook](/developers/cookbook) or implement directly against the core API.

## Related

- [Core API](/developers/api/core-api) — package reference
- [Extension profiles](/developers/extension-profiles) — manifest tokens
- [Javadoc](/developers/reference) — every public type and method
