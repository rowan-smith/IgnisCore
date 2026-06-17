---
title: Core API
description: dev.rono.igniscore.api — strategies, ports, models, and services.
slug: /developers/api/core-api
---

The core API is the stable contract between IgnisCore and extension JARs.

## Maven

```xml
<dependency>
  <groupId>dev.rono</groupId>
  <artifactId>api</artifactId>
  <version>%%site.version%%</version>
  <scope>provided</scope>
</dependency>
```

## Package map

| Package | Purpose |
|---------|---------|
| `dev.rono.igniscore.api` | `IgnisCoreAPI`, `IgnisCoreFacade`, version info |
| `dev.rono.igniscore.api.strategy` | `IgnisBlockStrategy`, `IgnisItemStrategy`, abstracts |
| `dev.rono.igniscore.api.model` | `BlockDefinition`, `ItemDefinition`, runtime instances |
| `dev.rono.igniscore.api.port` | Platform-neutral player, world, item, block abstractions |
| `dev.rono.igniscore.api.config` | YAML config parsers and behavior config types |
| `dev.rono.igniscore.api.service` | NBT, protocol, and effect services |
| `dev.rono.igniscore.api.extension` | Manifest and resource descriptors |

## Key entry points

| Class | Purpose |
|-------|---------|
| [IgnisCoreAPI](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/IgnisCoreAPI.html) | Runtime facade for integrations |
| [AbstractIgnisBlockStrategy](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/AbstractIgnisBlockStrategy.html) | Base class for block extensions |
| [AbstractIgnisItemStrategy](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/AbstractIgnisItemStrategy.html) | Base class for item extensions |
| [IgnisStrategyContext](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/IgnisStrategyContext.html) | Services injected into strategies |
| [StrategySupport](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/StrategySupport.html) | Read typed values from `custom_data` maps |

## Rules

- Use `provided` scope — never shade the API into extension JARs
- Declare matching `api-version` in your manifest — see [API versioning](/faq/api-version)
- Platform types (Bukkit `Player`, Sponge equivalents) stay inside adapters — use `IgnisPlayer` etc.

## Related

- [Extension config](/developers/extension-config) — `config.yml` reference
- [Strategies](/concepts/strategies) — lifecycle overview
- [Javadoc hub](/developers/reference) — browse all classes
