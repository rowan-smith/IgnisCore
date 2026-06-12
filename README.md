# IgnisCore

IgnisCore is a multi-platform framework for custom explosive blocks and throwable items. It uses a platform-neutral core with version-specific adapters discovered at runtime via `ServiceLoader`.

## Module layout

```
igniscore-parent/
├── api/                    Platform-neutral public contract (ports, strategies, models)
├── common/                 Shared runtime: extension loading, strategy registry, resource packs
├── extensions/
│   ├── blocks/             Platform-agnostic block extension JARs
│   └── items/              Platform-agnostic item extension JARs
├── spigot/
│   ├── v1.21.x/            Spigot 1.21.x runtime + adapter
│   └── v1.20.x/            Spigot 1.20.x bootloader
├── paper/
│   ├── v1.21.x/            Paper 1.21.x adapter + bootloader
│   └── v1.20.x/            Paper 1.20.x bootloader
├── folia/
│   └── v1.21.x/            Folia 1.21.x adapter + region scheduler
├── sponge/
│   ├── v12.0.0/            SpongeAPI 12.x / MC 1.21.x adapter + runtime
│   └── v8.5.0/             Reserved for SpongeAPI 8.x / MC 1.20.x (stub)
├── bootstrap/              Bukkit/Paper/Folia deployable JAR
└── sponge-bootstrap/       SpongeVanilla deployable JAR
```

| Module | Purpose |
|--------|---------|
| `api` | Stable extension-facing contract: `IgnisCoreAPI`, ports, strategy interfaces |
| `common` | Extension loaders, strategy registry/bootstrap, `ItemManager`, resource pack builder, `IgnisCommonModule` |
| `extensions/*` | Self-contained block/item JARs depending only on `api` |
| `spigot/v1.21.x` | Bukkit runtime, listeners, services, schedulers |
| `paper/v1.21.x` | Paper data-component hooks, `PaperV121Bootloader` |
| `folia/v1.21.x` | Folia region scheduler, `FoliaV121Bootloader` (priority 150) |
| `sponge/v12.0.0` | Sponge adapter, extension loading, `/ignis` command |
| `bootstrap` | `igniscore-<version>.jar` for Spigot/Paper/Folia |
| `sponge-bootstrap` | `igniscore-sponge-<version>.jar` for SpongeVanilla |

## Build output

```bash
mvn clean package
```

| Platform | Deploy |
|----------|--------|
| Spigot / Paper / Folia | `bootstrap/target/igniscore-1.0.0.jar` |
| SpongeVanilla | `sponge-bootstrap/target/igniscore-sponge-1.0.0.jar` |

## Bootloader priority (Bukkit family)

On startup, `IgnisBootstrapPlugin` selects the highest-priority matching `PlatformBootloader`:

| Priority | Bootloader | When |
|----------|------------|------|
| 150 | `FoliaV121Bootloader` | Folia + MC 1.21.x |
| 100 | `PaperV121Bootloader` / `PaperV120Bootloader` | Paper + matching MC line |
| 50 | `SpigotV121Bootloader` / `SpigotV120Bootloader` | Spigot + matching MC line |

Folia uses `FoliaIgnisScheduler` (region-scoped tasks via `RegionScheduler` / `GlobalRegionScheduler`).

## Platform ports

Extension strategies use neutral types from `dev.rono.igniscore.api.port`:

- `IgnisLocation`, `IgnisPlayer`, `IgnisItem`, `IgnisBlock`, `IgnisInteraction`
- `IgnisScheduler` via `IgnisStrategyContext.getScheduler()`
- World operations via `IgnisWorld` / `ExtensionSupport.resolveWorld()`

## Requirements

**Bukkit family:** Spigot, Paper, or Folia 1.20.x / 1.21.x — Java 25, NBTAPI (required), ProtocolLib (optional)

**Sponge:** SpongeVanilla 1.21.x with SpongeAPI 12.x — Java 25
