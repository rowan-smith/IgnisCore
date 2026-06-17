---
title: Extension Cookbook
description: Copy-paste recipes for IgnisCore block and item extensions using the core API only.
slug: /developers/cookbook
---

Short, task-oriented recipes using **`dev.rono:api` only**. Link to Javadoc for full signatures. Sample extensions in `extensions/` may use additional internal helpers; the patterns below show the raw approach.

---

## Minimal block strategy

```java
public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
    }

    @Override
    public StrategyProfile profile(BlockDefinition definition) {
        return StrategyProfile.defaults();
    }
}
```

**Maven:** `dev.rono:api:%%site.version%%` (provided)

**Javadoc:** [AbstractIgnisBlockStrategy](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/AbstractIgnisBlockStrategy.html)

---

## Read `custom_data` (core API)

```java
Map<String, Object> data = definition.getCustomData();
int fuse = StrategySupport.customInt(data, "fuse", 80);
float power = (float) StrategySupport.customDouble(data, "power", 4.0);
boolean fire = StrategySupport.customBoolean(data, "fire", false);
```

Block example `config.yml`:

```yaml
custom_data:
  fuse: 60
  power: 3.5
  fire: false
```

Only declare keys your strategy reads — see [Extension config](/developers/extension-config).

**Javadoc:** [StrategySupport](pathname:///apidocs/%%site.version%%/dev/rono/igniscore/api/strategy/StrategySupport.html)

**Sample:** [extensions/blocks/nuke](https://github.com/%%site.repo%%/tree/main/extensions/blocks/nuke)

---

## Explosive fuse block

Strategy profile for combustible blocks:

```java
@Override
public StrategyProfile profile(BlockDefinition definition) {
    int fuse = StrategySupport.customInt(definition.getCustomData(), "fuse", 80);
    return StrategyProfile.builder()
            .defaultFuse(fuse)
            .build();
}
```

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
    IgnisWorld world = context.getExtensionSupport().resolveWorld(instance.getLocation());
    float power = (float) StrategySupport.customDouble(instance.getDefinition().getCustomData(), "power", 4.0);
    boolean fire = StrategySupport.customBoolean(instance.getDefinition().getCustomData(), "fire", false);
    world.createExplosion(instance.getLocation(), power, fire, true);
}
```

**Sample:** [extensions/blocks/splitter-charge](https://github.com/%%site.repo%%/tree/main/extensions/blocks/splitter-charge)

---

## Throwable item (raw)

```java
@Override
public void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item,
                       IgnisInteraction action, IgnisBlock clickedBlock) {
    Map<String, Object> data = definition.getCustomData();
    double speed = StrategySupport.customDouble(data, "throw_velocity", 1.2);
    int fuseTicks = StrategySupport.customInt(data, "fuse_ticks", 40);

    IgnisLocation eye = player.getEyeLocation();
    Object projectile = player.getWorld().spawnProjectile("snowball", eye, player, 0, 0, speed);
    item.setAmount(item.getAmount() - 1);

    int[] ticks = {0};
    IgnisTask[] task = {null};
    task[0] = context.getScheduler().runRepeating(eye, () -> {
        ticks[0]++;
        if (!player.getWorld().isEntityValid(projectile) || ticks[0] >= fuseTicks) {
            IgnisLocation impact = player.getWorld().getEntityLocation(projectile);
            float power = (float) StrategySupport.customDouble(data, "power", 4.0);
            player.getWorld().createExplosion(impact, power, false, true);
            if (player.getWorld().isEntityValid(projectile)) {
                player.getWorld().removeEntity(projectile);
            }
            task[0].cancel();
        }
    }, 1L, 1L);
}
```

**Sample:** [extensions/items/grenade](https://github.com/%%site.repo%%/tree/main/extensions/items/grenade)

---

## Link item → block (NBT, raw)

Store a block location on the held item, activate remotely on right-click air:

```java
void onItemUse(IgnisPlayer player, ItemDefinition definition, IgnisItem item, IgnisBlock clickedBlock) {
    IgnisNbtService nbt = context.getNbtService();
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
    if (world == null || world.isBlank()) {
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
    IgnisNbtService nbt = context.getNbtService();
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
    long period = StrategySupport.customInt(definition.getCustomData(), "tickPeriod", 20);
    context.getScheduler().runRepeating(location, () -> tick(location), period, period);
}

void onPlacedBreak(BlockDefinition definition, IgnisLocation location) {
    // Cancel tasks keyed to location in your own registry, or use a single-shot guard flag
}
```

Placed utility blocks (`auto-sieve`, `socket-lamp`, `sprinkler-head`, …) use this pattern. Omit `custom_data` when code defaults are sufficient.

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
