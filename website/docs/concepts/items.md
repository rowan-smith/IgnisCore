---
title: Item Lifecycle
description: Custom throwable and clickable items in IgnisCore.
slug: /concepts/items
---

Custom items are standard inventory items tagged with IgnisCore NBT. Behavior is driven by YAML `behavior` and strategy code.

## Config sections

| Section | Purpose |
|---------|---------|
| `display` | Title and description |
| `item` | Base material and item flags |
| `textures` | Icon asset path |
| `behavior` | Click actions (assign, throw, detonate, etc.) |
| `custom_data` | Extension-specific tuning |

## Item behavior

```yaml
behavior:
  left_click_block: assign
  right_click_air: detonate
  right_click_block: detonate
```

Action tokens are interpreted by the item strategy (`throw`, `assign`, `detonate`, etc.).

## Strategy hook

Items use a single hook — branch on `IgnisInteraction` in strategy code:

```java
@Override
public void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                       IgnisInteraction action, IgnisBlock clickedBlock) {
    switch (action) {
        case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> throwItem(player, definition, item);
        default -> { }
    }
}
```

## Typed config

Use `extensions/shared` helpers for common patterns:

```java
ThrowableItemConfig throwable = ExtensionConfigs.throwable(definition);
double speed = throwable.throwVelocity();
int fuseTicks = throwable.fuseTicks();
```

## Related

- [Extensions](/concepts/extensions) — deploy and manifest
- [Extension Cookbook](/developers/cookbook) — throwable recipes
- [API Reference](/developers/api/core-api) — `IgnisItemStrategy`
