---
title: Extension Shared
description: dev.rono.extensions.shared — typed config helpers for common extension patterns.
slug: /developers/api/extension-shared
---

`extensions/shared` provides typed config accessors for common block and item patterns. Optional but recommended for explosive blocks and throwables.

## Maven

```xml
<dependency>
  <groupId>dev.rono.extensions</groupId>
  <artifactId>shared</artifactId>
  <version>%%site.version%%</version>
  <scope>provided</scope>
</dependency>
```

## ExtensionConfigs

| Method | Returns | Use case |
|--------|---------|----------|
| `ExtensionConfigs.explosion(definition)` | `ExplosionConfig` | Fuse, power, radius, fire |
| `ExtensionConfigs.throwable(definition)` | `ThrowableItemConfig` | Throw velocity, fuse ticks |

## Example

```java
import dev.rono.extensions.shared.config.ExtensionConfigs;
import dev.rono.extensions.shared.config.ExplosionConfig;

ExplosionConfig explosion = ExtensionConfigs.explosion(definition);
int fuse = explosion.fuse();
float power = explosion.resolvedPower();
```

Raw maps remain available via `definition.getCustomConfig()` when you need custom keys.

## Samples

- [extensions/blocks/nuke](https://github.com/%%site.repo%%/tree/main/extensions/blocks/nuke) — `ExplosionConfig`
- [extensions/items/grenade](https://github.com/%%site.repo%%/tree/main/extensions/items/grenade) — `ThrowableItemConfig`

## Related

- [Core API](/developers/api/core-api) — required base dependency
- [Extension Cookbook](/developers/cookbook) — recipes using these helpers
