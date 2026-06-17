---
title: Requirements
description: Java, server software, and dependency requirements for IgnisCore.
slug: /requirements
---

## Java

IgnisCore requires **Java 25**.

## Server software

One bootstrap JAR works on every supported platform. Each server loads only its own entry point via `ServiceLoader`.

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

Deploy **`bootstrap/target/%%site.jarName%%`**.

## Related

- [Configuration](/configuration) — resource pack host and performance tuning
- [Architecture](/developers/architecture) — module layout and boot flow
