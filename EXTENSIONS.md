# IgnisCore Extension Authoring Guide

This guide explains how to create **block** and **item** extensions for IgnisCore. Extensions are platform-agnostic JARs loaded at runtime from the server data folder.

## Quick start

Copy an existing extension under `extensions/blocks/` or `extensions/items/` as a template (for example `extensions/blocks/nuke` or `extensions/items/grenade`), rename packages and IDs, then build with Maven.

Build the project (`mvn package`) and copy the JAR to:

| Kind | Deploy path (under plugin data folder) |
|------|----------------------------------------|
| Block | `blocks/` |
| Item | `items/` |

Bundled extensions ship inside the bootstrap JAR and extract on first boot; drop-in JARs in these folders override or add types.

## Project layout

```
my-extension/
├── pom.xml                         # api dependency (provided scope)
├── src/main/java/.../Strategy.java # required strategy class
├── src/main/resources/
│   ├── block-extension.yml         # or item-extension.yml
│   ├── config.yml                  # visuals + tuning
│   └── textures/                   # PNG assets
└── src/test/java/...               # StrategyTest + BehaviorTest
```

## Two identifiers

| Field | Source | Purpose |
|-------|--------|---------|
| **Manifest `id`** | `*-extension.yml` | Registers the strategy in `IgnisStrategyRegistry` |
| **Config `id`** | `config.yml` | In-game type id (`/ignis give`, NBT type key) |

These are usually the same (e.g. `nuke`). Hyphens in ids become package segments without hyphens (`quarry-cache` → `quarrycache`).

## Manifest (`block-extension.yml` / `item-extension.yml`)

```yaml
id: my-tnt
name: My TNT
version: 1.0.0
api-version: 1.0.0        # Ignis API semver (see below)
author: YourName
strategy: dev.rono.igniscore.block.mytnt.Strategy
```

### API version (semver)

`api-version` declares the **Ignis API** your extension was built against, not your extension's release version.

- Runtime **1.2.0** loads extensions targeting **1.0.0** on the same major line.
- Extensions targeting **1.3.0** fail on runtime **1.2.0**.
- Major versions must match (**2.x** extensions do not load on **1.x**).

Current API version: **`1.0.0`** (`IgnisApiVersion.CURRENT`).

## Strategy class contract

Every extension JAR must provide a public class listed in the manifest:

```java
public class Strategy extends AbstractIgnisBlockStrategy {
    public Strategy(IgnisStrategyContext context) {
        super(context);
    }
}
```

Items extend `AbstractIgnisItemStrategy` instead.

`IgnisStrategyContext` exposes scheduler, NBT, protocol, effects, and `ExtensionSupport` (inventories, drop collectors, world bridge).

## Config layout

Every extension `config.yml` uses the same top-level sections:

| Section | Purpose |
|---------|---------|
| `display` | Title and description |
| `block` / `item` | Placeable/breakable flags, base material, breaking tuning |
| `textures` | Asset paths |
| `behavior` | **Surface clicks only** — left/right block and air actions |
| `custom_data` | Extension-specific tuning (fuse, power, collect radius, etc.) |

Legacy `interactions` is still parsed for effect tuning (particles, etc.) but new extensions should put ignite sounds under `behavior.sounds`.

### Block `behavior`

```yaml
behavior:
  combustible: true
  left_click_block: break      # none | break | ignite | open | handled
  right_click_block: ignite
  left_click_air: none
  right_click_air: none
  ignition_materials:
    - FLINT_AND_STEEL
    - FIRE_CHARGE
    - FLINT
  sounds:
    place: BLOCK_BEACON_ACTIVATE
    ignite: ITEM_FLINTANDSTEEL_USE
```

The core merges `behavior` into the strategy profile at runtime. Override {@link IgnisBlockStrategy#onPlacedClick} only when you need logic beyond these actions.

### Item `behavior`

```yaml
behavior:
  left_click_block: assign
  right_click_air: detonate
  right_click_block: detonate
```

Action tokens are interpreted by the item strategy (`throw`, `assign`, `detonate`, etc.).

## Block lifecycle

Blocks have **placed** and **active** phases:

| Phase | When | Callbacks |
|-------|------|-----------|
| **Placed** | Barrier block exists in world | `onPlaced`, `onPlacedClick`, `onPlacedInteract`, `onPlacedBreak` |
| **Active** | After ignition / fuse | `onPlace`, `onTick`, `onTrigger` |

Surface click routing is declared in YAML {@code behavior}. Override {@link IgnisBlockStrategy#onPlacedClick} only for custom logic beyond the standard actions.

## Block lifecycle

```java
import dev.rono.extensions.shared.config.ExtensionConfigs;
import dev.rono.extensions.shared.config.ExplosionConfig;

ExplosionConfig explosion = ExtensionConfigs.explosion(definition);
int fuse = explosion.fuse();
float power = explosion.resolvedPower();
```

Raw maps remain available via `definition.getCustomConfig()`.

## Item lifecycle

Items use a single hook — branch on {@link IgnisInteraction} in strategy code:

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

### Typed item config (`extensions/shared`)

```java
import dev.rono.extensions.shared.config.ExtensionConfigs;
import dev.rono.extensions.shared.config.ThrowableItemConfig;

ThrowableItemConfig throwable = ExtensionConfigs.throwable(definition);
double speed = throwable.throwVelocity();
int fuseTicks = throwable.fuseTicks();
```

## Config.yml overview (blocks)

```yaml
id: my-tnt
display:
  title: "&cMy TNT"
  description: ["&7Boom"]
block:
  placeable: true
  breakable: true
  breaking:
    ticks: 0
textures:
  top: top.png
  side: side.png
  bottom: bottom.png
custom_data:
  fuse: 80
  radius: 4.0
  power: 4.0
  fire: false
interactions:
  ignite:
    sound: ITEM_FLINTANDSTEEL_USE
```

## Config.yml overview (items)

```yaml
id: my-grenade
display:
  title: "&cGrenade"
item:
  base_material: snowball
textures:
  icon: icon.png
custom_data:
  throw_velocity: 1.2
  fuse_ticks: 40
  power: 4.0
```

## Testing

Extension modules depend on `api` and `extensions/shared` (provided at runtime via the bootstrap plugin):

```xml
<dependency>
  <groupId>dev.rono.extensions</groupId>
  <artifactId>shared</artifactId>
  <scope>provided</scope>
</dependency>
<dependency>
  <groupId>dev.rono</groupId>
  <artifactId>api</artifactId>
  <type>test-jar</type>
  <scope>test</scope>
</dependency>
```

Minimum tests:

- **StrategyTest** — manifest id, strategy class loads, profile smoke
- **BehaviorTest** — exercise `onPlaced` / `onTrigger` / `onItemUse` with `BehaviorTestSupport`

## Reference extensions

| Block | Pattern |
|-------|---------|
| `extensions/blocks/nuke` | Explosive + fuse ticks + `ExplosionConfig` |
| `extensions/blocks/quarry-cache` | Non-combustible GUI block + `ExtensionSupport` |
| `extensions/blocks/tunneling-tnt` | Minimal `onTrigger`-only strategy |

| Item | Pattern |
|------|---------|
| `extensions/items/grenade` | Throwable + `ThrowableItemConfig` + `IgnisInteraction` branching |
| `extensions/items/detonator` | Multi-click item via `onItemUse` |

## Breaking changes (1.x)

- Block placed callbacks renamed from `onStaticPlace` / `onStaticInteract` / `onStaticBreak` to **`onPlaced`** / **`onPlacedInteract`** / **`onPlacedBreak`**.
- Click routing moved from YAML `interactions.*.action` to **`onPlacedClick`** (blocks) and **`onItemUse`** + **`IgnisInteraction`** (items). YAML `interactions` is optional tuning data only.
- **`ItemDefinition#interactionAction`** and **`InteractionSettings`** removed.
