---
title: Item Lifecycle
description: Custom items — throwables, consumables, link tools, and click-driven behavior.
slug: /concepts/items
---

Custom items are standard inventory items tagged with IgnisCore NBT. Behavior is driven by YAML `behavior` and strategy code — from throwable explosives and utility tools to consumables with cooldowns and link items that remote-control blocks.

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

Action tokens are interpreted by the item strategy. When `behavior` is configured in YAML, `IgnisItemStrategy` routes resolved tokens to **`onItemAction`** — override that hook instead of re-parsing `ItemBehaviorConfig` in most strategies.

Common tokens: `throw`, `assign`, `detonate`, `use`, `none`.

## Strategy hook

Items use a single hook — branch on the resolved behavior action token:

```java
@Override
public void onItemAction(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                         IgnisInteraction action, IgnisBlock clickedBlock, String actionToken) {
    if ("throw".equals(actionToken)) {
        throwItem(player, definition, item);
    } else if ("detonate".equals(actionToken)) {
        detonate(player, definition, item);
    }
}
```

Use `IgnisStrategies.items().actionFor(definition, action)` when you need the token outside the default routing.

## Read custom_data in strategy code

```java
int fuseTicks = IgnisStrategies.data().customInt(definition, "fuse_ticks", 40);
double speed = IgnisStrategies.data().customDouble(definition, "throw_velocity", 1.2);
```

Or use `ExtensionShared.config().throwable(definition)` for the standard throwable shape — see [Extension shared](/developers/api/extension-shared).

Omit the `custom_data` section entirely when your strategy uses code defaults only.

Link items may use remote-activation keys (`linkBlockType`, `remoteAction`, `linkRange`) or multi-link keys (`target_block`, `max_links`) — see [detonator](https://github.com/%%site.repo%%/tree/main/extensions/items/detonator) and [lamp-dimmer](https://github.com/%%site.repo%%/tree/main/extensions/items/lamp-dimmer).

## Related

- [Extensions](/concepts/extensions) — deploy and manifest
- [Extension config](/developers/extension-config) — config.yml reference
- [Extension Cookbook](/developers/cookbook) — throwable and consumable recipes
- [API Reference](/developers/api/core-api) — `IgnisItemStrategy`
