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
| `custom_data` | Optional — only keys your strategy reads |

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

## Read custom_data in strategy code

```java
int fuseTicks = StrategySupport.customInt(definition.getCustomData(), "fuse_ticks", 40);
double speed = StrategySupport.customDouble(definition.getCustomData(), "throw_velocity", 1.2);
```

Omit the `custom_data` section entirely when your strategy uses code defaults only.

## Related

- [Extensions](/concepts/extensions) — deploy and manifest
- [Extension config](/developers/extension-config) — config.yml reference
- [Extension Cookbook](/developers/cookbook) — throwable and consumable recipes
- [API Reference](/developers/api/core-api) — `IgnisItemStrategy`
