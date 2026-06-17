---
title: Extension Cookbook
description: Copy-paste recipes for IgnisCore block and item extensions using the core API only.
slug: /developers/cookbook
---

Short, task-oriented recipes for IgnisCore block and item extensions. Prefer **`IgnisStrategies`** for core strategy helpers and **`ExtensionShared`** for optional shared helpers. Raw `StrategySupport` and port calls are shown where they clarify the underlying behavior.

---

## Minimal block strategy

```java
public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return IgnisStrategies.blocks().placed();
    }
}
```

**Maven:** `dev.rono:api:%%site.version%%` (provided)

**Javadoc:** [AbstractIgnisBlockStrategy](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/AbstractIgnisBlockStrategy.html)

---

## Read `custom_data` (core API)

```java
Map<String, Object> data = definition.getCustomData();
int fuse = IgnisStrategies.data().customInt(definition, "fuse", 0);
float power = (float) IgnisStrategies.data().customDouble(definition, "power", 4.0);
boolean fire = IgnisStrategies.data().customBoolean(definition, "fire", false);
```

`IgnisStrategies.data()` also accepts a raw `Map` if you already called `getCustomData()`. Fuse and radius are **opt-in** on `StrategyProfile` — use `IgnisStrategies.blocks().placed()` for utility blocks, `IgnisStrategies.blocks().fuse(ticks)` for fuse lifecycle blocks, or `IgnisStrategies.blocks().combustible(fuse, radius)` for ignitable explosives.

Block example `config.yml`:

```yaml
custom_data:
  fuse: 60
  power: 3.5
  fire: false
```

Only declare keys your strategy reads — see [Extension config](/developers/extension-config).

**Javadoc:** [IgnisStrategies](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/IgnisStrategies.html)

**Sample:** [extensions/blocks/nuke](https://github.com/%%site.repo%%/tree/main/extensions/blocks/nuke)

---

## Explosive fuse block

Strategy profile for combustible blocks:

```java
@Override
public StrategyProfile profile(BlockDefinition definition) {
    int fuse = StrategySupport.customInt(definition, "fuse", 80);
    return StrategyProfile.fuse(fuse);
}
```

Bundled fuse blocks often choose per-extension defaults in code (for example splitter-charge **60**, nuke **160**) while also storing fuse in `custom_data`.

`config.yml` behavior (combustible defaults to false — set explicitly for TNT):

```yaml
behavior:
  combustible: true
  left_click_block: break
  right_click_block: ignite
  ignition_materials:
    - FLINT_AND_STEEL
    - FIRE_CHARGE
custom_data:
  fuse: 60
```

Detonate in `onTrigger`:

```java
@Override
public void onTrigger(RuntimeBlockInstance instance, Object triggerContext) {
    IgnisWorld world = context.extensions().resolveWorld(instance.getLocation());
    float power = (float) StrategySupport.customDouble(instance.getDefinition(), "power", 4.0);
    boolean fire = StrategySupport.customBoolean(instance.getDefinition(), "fire", false);
    world.createExplosion(instance.getLocation(), power, fire, true);
}
```

**Sample:** [extensions/blocks/splitter-charge](https://github.com/%%site.repo%%/tree/main/extensions/blocks/splitter-charge)

---

## Throwable item

Override `onItemAction` and use `ExtensionShared` for typed config and detonation:

```java
@Override
public void onItemAction(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                         IgnisInteraction action, IgnisBlock clickedBlock, String actionToken) {
    if ("throw".equals(actionToken)) {
        behavior.onItemUse(player, definition, item);
    }
}

// In behavior code:
var throwable = ExtensionShared.config().throwable(definition);
// ... spawn projectile, then:
ExtensionShared.explosion().create(world, impact, definition, throwable.power(), throwable.fire());
```

**Sample:** [extensions/items/grenade](https://github.com/%%site.repo%%/tree/main/extensions/items/grenade)

---

## Link item → block (NBT, raw)

Store a block location on the held item, activate remotely on right-click air:

```java
void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
    IgnisNbtService nbt = context.nbt();
    String expectedType = StrategySupport.customString(definition.getCustomData(), "linkBlockType", "");

    if (clickedBlock != null) {
        IgnisLocation block = Locations.toBlock(clickedBlock.getLocation());
        nbt.setItemString(item, "ignis:link_world", block.worldName());
        nbt.setItemInt(item, "ignis:link_x", (int) block.x());
        nbt.setItemInt(item, "ignis:link_y", (int) block.y());
        nbt.setItemInt(item, "ignis:link_z", (int) block.z());
        nbt.setItemString(item, "ignis:link_type", expectedType);
        player.sendMessage("<aqua>Linked.</aqua>");
        return;
    }

    String world = nbt.getItemString(item, "ignis:link_world");
    if (world.isBlank()) {
        player.sendMessage("<red>Right-click a block to link first.</red>");
        return;
    }
    IgnisLocation target = new IgnisLocation(world,
            nbt.getItemInt(item, "ignis:link_x", 0),
            nbt.getItemInt(item, "ignis:link_y", 0),
            nbt.getItemInt(item, "ignis:link_z", 0));
    // Dispatch to your placed-block handler (registry, event, or direct strategy call)
}
```

`config.yml`:

```yaml
custom_data:
  linkBlockType: "socket-lamp"
  remoteAction: "cycle"
  linkRange: 64
```

**Samples:** [lamp-dimmer](https://github.com/%%site.repo%%/tree/main/extensions/items/lamp-dimmer), [socket-lamp](https://github.com/%%site.repo%%/tree/main/extensions/blocks/socket-lamp)

---

## Consumable with cooldown (NBT, raw)

```java
void consume(IgnisPlayer player, IgnisItem item, ItemDefinition definition) {
    IgnisNbtService nbt = context.nbt();
    String key = "ignis:cooldown:" + definition.getId();
    long cooldownTicks = StrategySupport.customInt(definition.getCustomData(), "cooldownTicks", 0);
    long last = nbt.getItemInt(item, key, 0);
    if (last > 0 && System.currentTimeMillis() - last < cooldownTicks * 50L) {
        player.sendMessage("<red>Still on cooldown.</red>");
        return;
    }
    player.applyPotionEffect("HASTE", 1200, 0);
    nbt.setItemInt(item, key, (int) System.currentTimeMillis());
    item.setAmount(item.getAmount() - 1);
}
```

**Sample:** [miners-lunch](https://github.com/%%site.repo%%/tree/main/extensions/items/miners-lunch)

---

## Placed block repeating tick (raw)

```java
void onPlaced(BlockDefinition definition, IgnisLocation location) {
    long period = StrategySupport.customInt(definition, "tickPeriod", 20);
    context.scheduler().runRepeating(location, () -> tick(location), period, period);
}

void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
    // Cancel tasks keyed to location in your own registry, or use a single-shot guard flag
}
```

Placed utility blocks (`auto-sieve`, `socket-lamp`, `sprinkler-head`, …) use this pattern. Declare only the `custom_data` keys your strategy reads (for example `tickPeriod`, `sieveParticles`).

**Sample:** [auto-sieve](https://github.com/%%site.repo%%/tree/main/extensions/blocks/auto-sieve)

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

Minimum tests: **StrategyTest** (manifest + profile smoke) and behavior tests for lifecycle callbacks.

---

## Related

- [Extension config](/developers/extension-config) — what belongs in `config.yml`
- [Extensions](/concepts/extensions) — deploy paths and identifiers
- [Core API](/developers/api/core-api) — package overview
- [API versioning](/faq/api-version) — semver rules
