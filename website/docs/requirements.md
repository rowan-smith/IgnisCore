---
title: Requirements
description: Java, server software, and dependency requirements for IgnisCore.
slug: /requirements
---

## Java

IgnisCore requires **Java 25**.

## Server software

One plugin JAR per server family, or use the universal JAR for all platforms. Spigot and Paper each have dedicated plugin manifests; Sponge uses `sponge_plugins.json`. Each server loads only its own entry point via `ServiceLoader`.

| Platform | Supported versions | Notes |
|----------|-------------------|-------|
| **Spigot** | 1.20.x, 1.21.x, 26.1.x | NBTAPI required |
| **Paper** | 1.20.x, 1.21.x, 26.1.x | NBTAPI required; Paper hooks used when available |
| **SpongeVanilla** | 1.20.x (API 8.5.x), 1.21.x (API 12.x), 26.1.x (API 19.x) | No NBTAPI |

## Dependencies (Bukkit family)

| Plugin | Required | Purpose |
|--------|----------|---------|
| [NBTAPI](https://www.spigotmc.org/resources/nbt-api.7939/) | Yes | Item and block NBT tagging |
| [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) | No | Enhanced protocol features when present |

## Permissions

| Permission | Default | Purpose |
|------------|---------|---------|
| `igniscore.admin` | op | All `/ignis` commands |

## Build from source

```bash
mvn clean package
```

Deploy the plugin JAR for your server software:

- **Universal (all platforms):** `universal/target/%%site.jarName%%`
- **Spigot:** `spigot/target/IgnisCore-Spigot-%%site.version%%.jar`
- **Paper:** `paper/target/IgnisCore-Paper-%%site.version%%.jar`
- **Sponge 8.5:** `sponge-v8.5.0/target/IgnisCore-Sponge-v8-%%site.version%%.jar`
- **Sponge 12:** `sponge-v12.0.0/target/IgnisCore-Sponge-v12-%%site.version%%.jar`
- **Sponge 19:** `sponge-v19.0.0/target/IgnisCore-Sponge-v19-%%site.version%%.jar`

## Related

- [Configuration](/configuration) — resource pack host and performance tuning
- [Architecture](/developers/architecture) — module layout and boot flow
