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
├── extensions/
│   ├── blocks/             Bundled block extension JARs
│   ├── items/              Bundled item extension JARs
│   └── shared/             Internal helpers for bundled extensions only
└── bootstrap/              Single deployable JAR
```

| Module | Purpose |
|--------|---------|
| `api` | Stable extension-facing contract: `IgnisCoreAPI`, ports, strategies |
| `common` | Extension loaders, strategy registry, `BlockManager`, lifecycle |
| `bootstrap` | Produces one deployable JAR for every supported server |

## Boot flow

All platforms use `PlatformBootloaderLoader` (`common`) to identify server software and select a `PlatformBootloader`:

- **Bukkit family:** `IgnisBootstrapPlugin` from `plugin.yml` → Guice + `IgnisCoreApplication`
- **Sponge:** version-matched `IgnisSpongePlugin` from `sponge_plugins.json` → same loader

### Bootloader priority (Bukkit)

| Priority | Bootloader | Server |
|----------|------------|--------|
| 100 | Paper bootloaders | Paper 26.1.x / 1.21.x / 1.20.x |
| 50 | Spigot bootloaders | Spigot 26.1.x / 1.21.x / 1.20.x |
| 200 | Sponge bootloaders | Sponge 19.x / 12.x / 8.5.x |

## Dependency rules

- Extensions depend on `api` with `provided` scope
- Third-party extensions should not depend on `extensions/shared` — use the [cookbook](/developers/cookbook) patterns instead
- Extensions must **not** shade IgnisCore or platform APIs into their JARs
- Platform adapters live in `bukkit/` and `sponge/` — extensions never import them

## Related

- [Developer overview](/developers) — quick start
- [API Reference](/developers/api/) — public packages
- [Requirements](/requirements) — supported platforms
