---
title: Extension Cookbook
description: Copy-paste recipes for IgnisCore block and item extensions.
slug: /developers/cookbook
---

Short, task-oriented recipes. Link to Javadoc for full signatures.

---

## Minimal block strategy

```java
public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
    }
}
```

**Maven:** `dev.rono:api:%%site.version%%` (provided)

**Javadoc:** [AbstractIgnisBlockStrategy](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/AbstractIgnisBlockStrategy.html)

---

## Explosion config (blocks)

```java
ExplosionConfig explosion = ExtensionConfigs.explosion(definition);
int fuse = explosion.fuse();
float power = explosion.resolvedPower();
```

**Maven:** `dev.rono.extensions:shared` (provided)

**Sample:** [extensions/blocks/nuke](https://github.com/%%site.repo%%/tree/main/extensions/blocks/nuke)

---

## Throwable item

```java
@Override
public void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                       IgnisInteraction action, IgnisBlock clickedBlock) {
    if (action == IgnisInteraction.RIGHT_CLICK_AIR) {
        ThrowableItemConfig cfg = ExtensionConfigs.throwable(definition);
        throwWithFuse(player, item, cfg.throwVelocity(), cfg.fuseTicks());
    }
}
```

**Javadoc:** [IgnisItemStrategy](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/IgnisItemStrategy.html)

**Sample:** [extensions/items/grenade](https://github.com/%%site.repo%%/tree/main/extensions/items/grenade)

---

## Block manifest

```yaml
id: my-tnt
name: My TNT
version: 1.0.0
api-version: 1.0.0
author: YourName
strategy: com.example.mytnt.Strategy
```

Place in `src/main/resources/block-extension.yml`.

---

## Test dependency

```xml
<dependency>
  <groupId>dev.rono</groupId>
  <artifactId>api</artifactId>
  <type>test-jar</type>
  <scope>test</scope>
</dependency>
```

Minimum tests: **StrategyTest** (manifest + profile smoke) and **BehaviorTest** (lifecycle callbacks).

---

## Related

- [Extensions](/concepts/extensions) — deploy paths and identifiers
- [API Reference](/developers/api/) — package overview
- [API versioning](/faq/api-version) — semver rules
