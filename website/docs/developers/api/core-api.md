---
title: Core API
description: dev.rono.igniscore.api — strategies, ports, models, and services.
slug: /developers/api/core-api
---

The core API is the stable contract between IgnisCore and extension JARs. Every public type and method is documented in [Javadoc](/developers/reference).

## Maven

```xml
<dependency>
  <groupId>dev.rono</groupId>
  <artifactId>api</artifactId>
  <version>%%site.version%%</version>
  <scope>provided</scope>
</dependency>
```

## API layers

See [API layers](/developers/api/layers) for the L1–L4 model. Extension authors live at **L2–L3** (`IgnisBlockStrategy` / `IgnisItemStrategy` + `IgnisStrategyContext`).

## Package map

| Package | Layer | Purpose |
|---------|-------|---------|
| `dev.rono.igniscore.api` | L4 | `IgnisCoreAPI`, `IgnisCoreFacade`, versioning |
| `dev.rono.igniscore.api.strategy` | L2–L3 | Strategies, `IgnisStrategyContext`, `ExtensionSupport` |
| `dev.rono.igniscore.api.model` | L2 | `BlockDefinition`, `ItemDefinition`, runtime instances |
| `dev.rono.igniscore.api.port` | L1 | `IgnisPlayer`, `IgnisWorld`, `IgnisItem`, scheduler |
| `dev.rono.igniscore.api.config` | L2 | YAML parsers, `BlockBehaviorConfig`, `ExtensionConfig` |
| `dev.rono.igniscore.api.service` | L3 | `IgnisNbtService`, `IgnisEffectService`, `IgnisProtocolService` |
| `dev.rono.igniscore.api.extension` | L2 | `ExtensionManifest`, profiles, integration requirements |
| `dev.rono.igniscore.api.inventory` | L3 | `IgnisCustomInventory` for extension GUIs |
| `dev.rono.igniscore.api.collection` | L3 | `IgnisDropCollector` for auto-collect blocks |
| `dev.rono.igniscore.api.util` | L1 | `Locations`, placement metadata helpers |

## Key entry points

| Class | Audience | Purpose |
|-------|----------|---------|
| [IgnisStrategyContext](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/IgnisStrategyContext.html) | Extensions | `scheduler()`, `nbt()`, `effects()`, `protocol()`, `extensions()` |
| [AbstractIgnisBlockStrategy](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/AbstractIgnisBlockStrategy.html) | Extensions | Base class for block JARs |
| [AbstractIgnisItemStrategy](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/AbstractIgnisItemStrategy.html) | Extensions | Base class for item JARs |
| [StrategySupport](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/StrategySupport.html) | Extensions | Read typed values from `custom_data` maps |
| [IgnisCoreAPI](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/IgnisCoreAPI.html) | Integrators | Runtime facade for other plugins |
| [ExtensionManifest](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/extension/ExtensionManifest.html) | Extensions | Parsed `*-extension.yml` metadata |

## IgnisStrategyContext accessors

```java
public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    void example(IgnisPlayer player) {
        context.scheduler().runLater(player.getLocation(), () -> { }, 20L);
        context.nbt().setItemString(item, "ignis:key", "value");
        context.effects().playSound(loc, "BLOCK_NOTE_BLOCK_PLING", 1f, 1f);
        if (context.protocol().isEnabled()) {
            context.protocol().sendFakeExplosion(loc, 2f, List.of(player));
        }
        context.extensions().openInventory(player, inventory);
    }
}
```

`getScheduler()`, `getNbtService()`, and other `get*` methods remain as aliases.

## Rules

- Use `provided` scope — never shade the API into extension JARs
- Declare matching `api-version` in your manifest — see [API versioning](/faq/api-version)
- Declare `profiles` and `requires-integrations` — see [Extension profiles](/developers/extension-profiles)
- Platform types stay inside adapters — use `IgnisPlayer` etc.

## Related

- [API layers](/developers/api/layers)
- [Extension profiles](/developers/extension-profiles)
- [Extension config](/developers/extension-config) — `config.yml` reference
- [Extension Cookbook](/developers/cookbook)
- [Javadoc](/developers/reference)
