# IgnisCore

IgnisCore is a multi-platform framework for custom explosive blocks and throwable items. It uses a platform-neutral core with server-software-specific adapters discovered at runtime via `ServiceLoader`.

## Module layout

```
igniscore-parent/
├── api/                    Platform-neutral public contract (ports, strategies, models)
├── common/                 Shared runtime: loaders, registry, BlockManager, lifecycle
├── bukkit/
│   ├── common/v1.21.x/     Shared Bukkit-family runtime (listeners, services, adapters)
│   ├── spigot/
│   │   ├── v1.21.x/        Spigot 1.21.x server software (bootloader)
│   │   └── v1.20.x/        Spigot 1.20.x server software (bootloader)
│   ├── paper/
│   │   ├── v1.21.x/        Paper 1.21.x server software (adapter + bootloader)
│   │   └── v1.20.x/        Paper 1.20.x server software (bootloader)
│   └── folia/v1.21.x/      Folia 1.21.x server software (adapter + region scheduler)
├── extensions/
│   ├── blocks/             Platform-agnostic block extension JARs
│   └── items/              Platform-agnostic item extension JARs
├── sponge/
│   ├── v12.0.0/            SpongeVanilla 12.x / MC 1.21.x server software
│   └── v8.5.0/             Reserved for SpongeAPI 8.x / MC 1.20.x (stub)
└── bootstrap/              Single deployable JAR for all server software
```

Each **server software** module (Spigot, Paper, Folia, Sponge) is independent. Version lines (`v1.21.x`, `v1.20.x`, `v12.0.0`) are separate modules for that software. Shared Bukkit logic lives in `bukkit/common/`, not in the Spigot module.

| Module | Purpose |
|--------|---------|
| `api` | Stable extension-facing contract: `IgnisCoreAPI`, ports, strategy interfaces |
| `common` | Extension loaders, strategy registry, `BlockManager`, `IgnisRuntimeLifecycle`, `IgnisCommonModule` |
| `bukkit/common/v1.21.x` | Bukkit listeners, commands, NBT/protocol/effect services, `BukkitPlatformAdapter` |
| `bukkit/spigot/v1.21.x` | Spigot 1.21.x `PlatformBootloader` only |
| `bukkit/paper/v1.21.x` | Paper 1.21.x adapter + bootloader (depends on `bukkit/common`, not `spigot`) |
| `bukkit/folia/v1.21.x` | Folia 1.21.x adapter, region scheduler, bootloader |
| `sponge/v12.0.0` | Sponge runtime, adapter, listeners, `/ignis` command |
| `bootstrap` | Produces one deployable JAR for every supported server (see Build output) |

## Build output

```bash
mvn clean package
```

Deploy **`bootstrap/target/igniscore-1.0.0.jar`** on any supported server. The JAR contains both Bukkit (`plugin.yml`) and Sponge (`META-INF/sponge_plugins.json`) entry descriptors; each server software loads only its own entry point, then `PlatformBootloaderLoader` selects the matching adapter (Spigot, Paper, Folia, or Sponge).

| Server software | Same artifact |
|-----------------|---------------|
| Spigot / Paper / Folia | `bootstrap/target/igniscore-1.0.0.jar` |
| SpongeVanilla | `bootstrap/target/igniscore-1.0.0.jar` |

## Boot flow

All platforms use `PlatformBootloaderLoader` (`common`) to identify server software and select a `PlatformBootloader` via `ServiceLoader`:

- **Bukkit family:** server loads `IgnisBootstrapPlugin` from `plugin.yml` → Guice + `IgnisCoreApplication`
- **Sponge:** server loads `IgnisSpongePlugin` from `sponge_plugins.json` → same loader → `SpongeIgnisApplication`

### Bootloader priority (Bukkit family)

| Priority | Bootloader | Server software |
|----------|------------|-----------------|
| 150 | `FoliaV121Bootloader` | Folia 1.21.x |
| 100 | `PaperV121Bootloader` / `PaperV120Bootloader` | Paper 1.21.x / 1.20.x |
| 50 | `SpigotV121Bootloader` / `SpigotV120Bootloader` | Spigot 1.21.x / 1.20.x |
| 200 | `SpongeV1200Bootloader` | Sponge 12.x / MC 1.21.x |

Folia uses `FoliaIgnisScheduler` (region-scoped tasks via reflection on Folia APIs).

## Platform ports

Extension strategies use neutral types from `dev.rono.igniscore.api.port`:

- **World / entities:** `IgnisLocation`, `IgnisPlayer`, `IgnisItem`, `IgnisBlock`, `IgnisInteraction`, `IgnisWorld`
- **Scheduling:** `IgnisScheduler` via `IgnisStrategyContext.getScheduler()`
- **Adapter surface:** `PlatformAdapter` (world resolution, items, blocks, commands, listeners)
- **Runtime host:** `IgnisRuntimeHost` (data directory, logger)
- **Visuals / items / packs:** `BlockVisualRenderer`, `IgnisCustomItemFactory`, `ResourcePackHost`
- **Lifecycle hooks:** `IgnisPlatformIntegration` (register listeners/commands on enable/disable)
- **Services:** `IgnisNbtService`, `IgnisProtocolService`, `IgnisEffectService` (platform-bound in Guice modules)

Shared enable/disable is handled by `IgnisRuntimeLifecycle` in `common/` (extensions, resource pack, cleanup).

## Requirements

**Spigot / Paper / Folia:** 1.20.x or 1.21.x — Java 25, NBTAPI (required), ProtocolLib (optional)

**SpongeVanilla:** 1.21.x with SpongeAPI 12.x — Java 25

## Persistence note

Placed custom blocks are stored in `placed-blocks.json`. Legacy `placed-blocks.yml` files are migrated automatically on first load.
