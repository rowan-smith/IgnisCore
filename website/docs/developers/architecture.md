---
title: Architecture
description: IgnisCore module layout, boot flow, and platform adapters.
slug: /developers/architecture
---

IgnisCore uses a platform-neutral core with server-software-specific adapters discovered at runtime via `ServiceLoader`.

## Module layout

```
igniscore-parent/
├── api/                    Platform-neutral public contract
├── common/                 Shared runtime: loaders, registry, BlockManager
├── bukkit/
│   ├── common/             Shared Bukkit runtime (listeners, commands, services)
│   ├── spigot/             Spigot bootloaders per MC version line
│   └── paper/              Paper adapter + bootloaders
├── sponge/
│   ├── common/             Shared Sponge runtime
│   └── v8.5.0 / v12.0.0 / v19.0.0   Version-specific entrypoints
└── bootstrap/
    ├── spigot/             Native Spigot plugin JAR (+ Sponge)
    └── paper/              Native Paper plugin JAR

[IgnisCore-Extensions](https://github.com/%%site.extensionsRepo%%) (separate repository, consumes API via JitPack)
├── blocks/                 Block extension modules
├── items/                  Item extension modules
└── shared/                 Optional helpers — ExtensionShared facade
```

Deploy built extension JARs to `plugins/IgnisCore/blocks/` and `plugins/IgnisCore/items/` on your server.

| Module | Purpose |
|--------|---------|
| `api` | Stable extension-facing contract: `IgnisCoreAPI`, ports, strategies |
| `common` | Extension loaders, strategy registry, `BlockManager`, lifecycle |
| `bootstrap/spigot` | Produces the Spigot/Sponge deployable JAR |
| `bootstrap/paper` | Produces the Paper-native deployable JAR |

## Boot flow

All platforms use `PlatformBootloaderLoader` (`common`) to identify server software and select a `PlatformBootloader`:

- **Spigot/Sponge:** `IgnisBootstrapPlugin` from `plugin.yml` → Guice + `IgnisCoreApplication`
- **Paper:** `IgnisPaperPlugin` from `plugin.yml` → same loader

### Bootloader priority (Bukkit family)

| Priority | Bootloader | Server |
|----------|------------|--------|
| 100 | Paper bootloaders | Paper 26.1.x / 1.21.x / 1.20.x |
| 50 | Spigot bootloaders | Spigot 26.1.x / 1.21.x / 1.20.x |

### Bootloader priority (Sponge)

| Priority | Bootloader | Server |
|----------|------------|--------|
| 200 | Sponge bootloaders | Sponge 19.x / 12.x / 8.5.x |

## Dependency rules

- Extensions depend on `api` with `provided` scope (via JitPack for external projects)
- Optional: depend on `shared` from [IgnisCore-Extensions](https://github.com/%%site.extensionsRepo%%) with `provided` scope and use `ExtensionShared.*()` — see [Extension shared](/developers/api/extension-shared)
- Extensions must **not** shade IgnisCore or platform APIs into their JARs
- Platform adapters live in `bukkit/` and `sponge/` — extensions never import them
- Extension authors use **L2–L3** (`IgnisStrategyContext`); integrators use **L4** (`IgnisCoreAPI`) — see [API layers](/developers/api/layers)

## Related

- [Developer overview](/developers) — quick start
- [API layers](/developers/api/layers) — L1–L4 surfaces
- [API Reference](/developers/api/) — public packages
- [Requirements](/requirements) — supported platforms
