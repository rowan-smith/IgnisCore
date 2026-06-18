---
title: Extension Shared
description: Optional helpers for extension authors — use ExtensionShared grouped accessors.
slug: /developers/api/extension-shared
---

The `shared` module in [IgnisCore-Extensions](https://github.com/%%site.extensionsRepo%%) ships optional helpers used by bundled extensions. Use the **`ExtensionShared`** facade for grouped, API-style access — do not import `*Support` classes directly.

## Maven

Build `shared` from [IgnisCore-Extensions](https://github.com/%%site.extensionsRepo%%) and depend on it with `provided` scope in your extension project. When bundling the plugin, copy `shared.jar` to `bootstrap/bundled/lib/shared.jar` so IgnisCore can shade it into the runtime.

```xml
<dependency>
  <groupId>dev.rono.extensions</groupId>
  <artifactId>shared</artifactId>
  <version>%%site.version%%</version>
  <scope>provided</scope>
</dependency>
```

The bootstrap plugin supplies this module at runtime when bundled. Do **not** shade it into extension JARs.

## Entry point

| Accessor | Purpose |
|----------|---------|
| `ExtensionShared.explosion()` | Fuse, power, and detonation helpers |
| `ExtensionShared.config()` | Typed views of common `custom_data` shapes |
| `ExtensionShared.theatrics()` | Particles, sounds, scan visuals |
| `ExtensionShared.ticks()` | Repeating tasks for placed blocks |
| `ExtensionShared.scan()` | Ore/crop/copper world scans |
| `ExtensionShared.link()` | Item ↔ block NBT linking |
| `ExtensionShared.remote()` | Remote activation registry |
| `ExtensionShared.consumable()` | Cooldowns and consume-one |
| `ExtensionShared.entities()` | Entity queries and radius effects |
| `ExtensionShared.processing()` | Processing GUI inventory helpers |
| `ExtensionShared.variants()` | Specialized blast patterns |
| `ExtensionShared.gui()` | Block storage and trade registries |

## Examples

```java
// Typed throwable config from custom_data
var throwable = ExtensionShared.config().throwable(definition);
double velocity = throwable.throwVelocity();
int fuseTicks = throwable.fuseTicks();

// Detonation with YAML overrides
ExtensionShared.explosion().create(world, location, definition, 4.0, false);
```

## When to use core API instead

| Task | Prefer |
|------|--------|
| Read arbitrary YAML keys | `IgnisStrategies.data()` or `StrategySupport` |
| Simple world explosion | `IgnisWorld.createExplosion(...)` |
| Item NBT | `IgnisNbtService` via `IgnisStrategyContext` |
| Repeating placed-block ticks | `IgnisScheduler` via `IgnisStrategyContext` |

See [Extension config](/developers/extension-config) for which `config.yml` keys to declare.

## Related

- [IgnisStrategies](/developers/api/core-api) — core strategy facade (`blocks()`, `items()`, `data()`)
- [Extension Cookbook](/developers/cookbook) — recipes with and without shared helpers
- [Core API](/developers/api/core-api) — required dependency
- [Architecture](/developers/architecture) — module layout
