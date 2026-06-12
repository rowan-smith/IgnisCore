# IgnisCore

IgnisCore is a multi-platform framework for custom explosive blocks and throwable items. It uses a platform-neutral core with version-specific adapters discovered at runtime via `ServiceLoader`.

## Module layout

```
igniscore-parent/
├── api/                    Platform-neutral public contract (ports, strategies, models)
├── common/                 Shared bootstrap utilities (PlatformBootloaderLoader)
├── extensions/
│   ├── blocks/             Platform-agnostic block extension JARs
│   └── items/              Platform-agnostic item extension JARs
├── spigot/
│   ├── v1.21.x/            Spigot 1.21.x adapter + bootloader
│   └── v1.20.x/            Reserved for Spigot 1.20.x (stub)
├── paper/
│   ├── v1.21.x/            Paper 1.21.x adapter + bootloader
│   └── v1.20.x/            Reserved for Paper 1.20.x (stub)
├── sponge/
│   ├── v12.0.0/            Reserved for SpongeAPI 12.x / MC 1.21.x (stub)
│   └── v8.5.0/             Reserved for SpongeAPI 8.x / MC 1.20.x (stub)
└── bootstrap/              Deployable plugin JAR (combines adapters + bundled extensions)
```

| Module | Purpose |
|--------|---------|
| `api` | Stable extension-facing contract: `IgnisCoreAPI`, ports (`IgnisLocation`, `IgnisPlayer`, …), strategy interfaces |
| `common` | Platform-neutral helpers shared by all adapters |
| `extensions/*` | Self-contained block/item JARs depending only on `api` |
| `spigot/v1.21.x` | Bukkit/Spigot runtime, listeners, services, `SpigotV121Bootloader` |
| `paper/v1.21.x` | Paper enhancements, `PaperV121Bootloader` (higher priority than Spigot) |
| `bootstrap` | Final `igniscore-<version>.jar` with hooked bootloaders and bundled extensions |

## Build output

From the repository root:

```bash
mvn clean package
```

Deploy:

```
bootstrap/target/igniscore-1.0.0.jar
```

At startup, `IgnisBootstrapPlugin` selects the highest-priority `PlatformBootloader` on the classpath (Paper before Spigot on Paper servers).

## Platform ports

Extension strategies use platform-neutral types from `dev.rono.igniscore.api.port`:

- `IgnisLocation`, `IgnisPlayer`, `IgnisItem`, `IgnisBlock`, `IgnisInteraction`
- `IgnisScheduler` via `IgnisStrategyContext.getScheduler()`
- World operations via `IgnisWorld` / `ExtensionSupport.resolveWorld()`

Each platform/version module implements `PlatformBootloader` and `PlatformAdapter`.

## Requirements

- Spigot or Paper 1.21+ (via bootstrap JAR)
- Java 25
- NBTAPI (required), ProtocolLib (optional)
