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
├── bukkit-common/          Shared Bukkit runtime (listeners, commands, services)
├── spigot/                 Spigot plugin JAR
├── paper/                  Paper plugin JAR
├── sponge-common/          Shared Sponge runtime
├── sponge-v8.5.0/          SpongeAPI 8.5 plugin (Minecraft 1.20.x)
├── sponge-v12.0.0/         SpongeAPI 12 plugin (Minecraft 1.21.x)
├── sponge-v19.0.0/         SpongeAPI 19 plugin (Minecraft 26.1.x)
└── universal/              Universal plugin JAR (all platforms)

[IgnisCore-Extensions](https://github.com/%%site.extensionsRepo%%) (separate repository, consumes API via JitPack)
├── blocks/                 Block extension modules
├── items/                  Item extension modules
└── shared/                 Optional helpers — ExtensionShared facade
```

Deploy built extension JARs to `plugins/IgnisCore/blocks/` and `plugins/IgnisCore/items/` on your server.

| Module | Output JAR |
|--------|------------|
| `spigot` | `IgnisCore-Spigot-<version>.jar` |
| `paper` | `IgnisCore-Paper-<version>.jar` |
| `sponge-v8.5.0` | `IgnisCore-Sponge-v8-<version>.jar` |
| `sponge-v12.0.0` | `IgnisCore-Sponge-v12-<version>.jar` |
| `sponge-v19.0.0` | `IgnisCore-Sponge-v19-<version>.jar` |
| `universal` | `IgnisCore-<version>.jar` |

## Boot flow

All platforms use `PlatformBootloaderLoader` (`common`) to identify server software and select a `PlatformBootloader`:

- **Spigot:** `IgnisCorePlugin` from `plugin.yml` → Guice + `IgnisCoreApplication`
- **Paper:** `IgnisPaperPlugin` from `paper-plugin.yml` → same loader
- **Sponge:** version-specific `IgnisSpongePlugin` from `sponge_plugins.json`

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
- Platform adapters live in `bukkit-common`, `spigot`, `paper`, and `sponge-*` — extensions never import them
- Extension authors use **L2–L3** (`IgnisStrategyContext`); integrators use **L4** (`IgnisCoreAPI`) — see [API layers](/developers/api/layers)

## Related

- [Developer overview](/developers) — quick start
- [API layers](/developers/api/layers) — L1–L4 surfaces
- [API Reference](/developers/api/) — public packages
- [Requirements](/requirements) — supported platforms
