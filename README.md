# IgnisCore

IgnisCore is a multi-platform framework for custom explosive blocks and throwable items. It uses a platform-neutral core with server-software-specific adapters discovered at runtime via `ServiceLoader`.

## Module layout

```
igniscore-parent/
├── api/                    Platform-neutral public contract (ports, strategies, models)
├── common/                 Shared runtime: loaders, registry, BlockManager, lifecycle
├── bukkit/
│   ├── common/             Shared Bukkit-family runtime (listeners, services, adapters)
│   ├── spigot/             Spigot bootloaders for all supported MC version lines
│   └── paper/              Paper adapter + bootloaders for all supported MC version lines
├── sponge/
│   ├── common/             Shared Sponge runtime (adapters, listeners, services)
│   ├── v8.5.0/             SpongeAPI 8.x entrypoint for MC 1.20.x
│   ├── v12.0.0/            SpongeAPI 12.x entrypoint for MC 1.21.x
│   └── v19.0.0/            SpongeAPI 19.x entrypoint for MC 26.1.x
├── extensions/
│   ├── blocks/             Platform-agnostic block extension JARs
│   └── items/              Platform-agnostic item extension JARs
└── bootstrap/              Single deployable JAR for all server software
```

| Module | Purpose |
|--------|---------|
| `api` | Stable extension-facing contract: `IgnisCoreAPI`, ports, strategy interfaces |
| `common` | Extension loaders, strategy registry, `BlockManager`, `IgnisRuntimeLifecycle` |
| `bukkit/common` | Bukkit listeners, commands, NBT/protocol/effect services, `BukkitPlatformAdapter` |
| `bukkit/spigot` | All Spigot `PlatformBootloader` implementations (1.20.x, 1.21.x, 26.1.x) |
| `bukkit/paper` | Paper adapter, hooks, and bootloaders for all supported MC version lines |
| `sponge/common` | Shared Sponge adapter, listeners, command, and Guice wiring |
| `sponge/v*.0` | Version-specific plugin entrypoint + bootloader per SpongeAPI line |
| `bootstrap` | Produces one deployable JAR for every supported server |

## Build output

```bash
mvn clean package
```

Deploy **`bootstrap/target/igniscore-1.0.0.jar`** on any supported server. The JAR contains both Bukkit (`plugin.yml`) and Sponge (`META-INF/sponge_plugins.json`) entry descriptors; each server software loads only its own entry point, then `PlatformBootloaderLoader` selects the matching adapter.

| Server software | Same artifact |
|-----------------|---------------|
| Spigot / Paper | `bootstrap/target/igniscore-1.0.0.jar` |
| SpongeVanilla | `bootstrap/target/igniscore-1.0.0.jar` |

## Boot flow

All platforms use `PlatformBootloaderLoader` (`common`) to identify server software and select a `PlatformBootloader` via `ServiceLoader`:

- **Bukkit family:** server loads `IgnisBootstrapPlugin` from `plugin.yml` → Guice + `IgnisCoreApplication`
- **Sponge:** server loads the version-matched `IgnisSpongePlugin` from `sponge_plugins.json` → same loader → `SpongeIgnisApplication`

### Bootloader priority (Bukkit family)

| Priority | Bootloader | Server software |
|----------|------------|-----------------|
| 100 | `PaperV261Bootloader` / `PaperV121Bootloader` / `PaperV120Bootloader` | Paper 26.1.x / 1.21.x / 1.20.x |
| 50 | `SpigotV261Bootloader` / `SpigotV121Bootloader` / `SpigotV120Bootloader` | Spigot 26.1.x / 1.21.x / 1.20.x |
| 200 | `SpongeV1900Bootloader` / `SpongeV1200Bootloader` / `SpongeV850Bootloader` | Sponge 19.x / 12.x / 8.5.x |

## Requirements

**Spigot / Paper:** 1.20.x, 1.21.x, or 26.1.x — Java 25, NBTAPI (required), ProtocolLib (optional)

**SpongeVanilla:** 1.20.x (SpongeAPI 8.5.x), 1.21.x (SpongeAPI 12.x), or 26.1.x (SpongeAPI 19.x) — Java 25

## Persistence note

Placed custom blocks are stored in `placed-blocks.json`. Legacy `placed-blocks.yml` files are migrated automatically on first load.

## Extension authoring

See **[EXTENSIONS.md](EXTENSIONS.md)** for manifest/config YAML, block/item lifecycles, semver `api-version`, and testing patterns.
