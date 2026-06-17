---
title: Item Lifecycle
description: Custom items — throwables, consumables, link tools, and click-driven behavior.
slug: /concepts/items
---

Custom items are standard inventory items tagged with IgnisCore NBT. Behavior is driven by YAML `behavior` and event bus subscriptions in strategy code — from throwable explosives and utility tools to consumables with cooldowns and link items that remote-control blocks.

## Config sections

| Section | Purpose |
|---------|---------|
| `display` | Title and description |
| `item` | Base material and item flags |
| `textures` | Icon asset path |
| `behavior` | Click actions (assign, throw, detonate, etc.) |
| `custom_data` | Optional — only keys your strategy reads |

## Item behavior

```yaml
behavior:
  left_click_block: assign
  right_click_air: detonate
  right_click_block: detonate
```

The core resolves YAML tokens and fires **`onItemClick`** on the event bus. Subscribe in `registerEvents()` and branch on `event.actionToken()`.

Common tokens: `throw`, `assign`, `detonate`, `use`, `none`.

## Strategy hook

```java
@Override
public void registerEvents() {
    onItemClick(event -> {
        if ("throw".equals(event.actionToken())) {
            throwItem(event.player(), event.definition(), event.item());
        } else if ("detonate".equals(event.actionToken())) {
            detonate(event.player(), event.definition(), event.item());
        }
    });
}
```

Integrators can observe the same events globally:

```java
IgnisCoreAPI.eventBus().subscribe(event -> {
    // observe all item clicks
});
```

## Read custom_data in strategy code

```java
int fuseTicks = IgnisStrategies.data().customInt(definition, "fuse_ticks", 40);
double speed = IgnisStrategies.data().customDouble(definition, "throw_velocity", 1.2);
```

Or use `ExtensionShared.config().throwable(definition)` for the standard throwable shape — see [Extension shared](/developers/api/extension-shared).

## Related

- [Extensions](/concepts/extensions) — deploy and manifest
- [Extension config](/developers/extension-config) — config.yml reference
- [Extension Cookbook](/developers/cookbook) — throwable and consumable recipes
- [API Reference](/developers/api/core-api) — event bus and strategies
